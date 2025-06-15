package com.example.projectakhirpam

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityAddStockoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

class AddStockOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddStockoutBinding
    private lateinit var database: DatabaseReference
    private lateinit var itemList: MutableList<Item>
    private lateinit var itemAdapter: ArrayAdapter<String>
    private var selectedItem: Item? = null
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStockoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if (uid == null) {
            Toast.makeText(this, "User belum login", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        database = FirebaseDatabase.getInstance().reference
        itemList = mutableListOf()

        loadItems(uid)

        // Tombol kembali
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Pilih tanggal
        binding.etDate.setOnClickListener {
            showDatePicker()
        }

        // Simpan data
        binding.btnSave.setOnClickListener {
            val qtyText = binding.etQty.text.toString().trim()
            val dateText = binding.etDate.text.toString().trim()
            val destText = binding.etDest.text.toString().trim()
            val noteText = binding.etNote.text.toString().trim()

            if (selectedItem == null || qtyText.isEmpty() || dateText.isEmpty()) {
                Toast.makeText(this, "Lengkapi semua field", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val qty = qtyText.toIntOrNull()
            if (qty == null || qty <= 0) {
                Toast.makeText(this, "Jumlah tidak valid", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (selectedItem!!.stock < qty) {
                Toast.makeText(this, "Stok tidak mencukupi", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val stockoutRef = database.child("users").child(uid).child("stockouts")
            val itemRef = database.child("users").child(uid).child("items").child(selectedItem!!.id)
            val stockoutId = stockoutRef.push().key ?: return@setOnClickListener
            val timestamp = System.currentTimeMillis()
            val totalPrice = selectedItem!!.price * qty

            val stockoutData = mapOf(
                "id" to stockoutId,
                "itemId" to selectedItem!!.id,
                "itemName" to selectedItem!!.name,
                "qty" to qty,
                "unitPrice" to selectedItem!!.price,
                "totalPrice" to totalPrice,
                "date" to dateText,
                "destination" to destText,
                "note" to noteText,
                "timestamp" to timestamp
            )

            val updates = hashMapOf<String, Any>(
                "/users/$uid/stockouts/$stockoutId" to stockoutData,
                "/users/$uid/items/${selectedItem!!.id}/stock" to (selectedItem!!.stock - qty)
            )

            database.updateChildren(updates)
                .addOnSuccessListener {
                    Toast.makeText(this, "Stock out berhasil disimpan", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun loadItems(uid: String) {
        val ref = database.child("users").child(uid).child("items")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                itemList.clear()
                val itemNames = mutableListOf<String>()

                for (child in snapshot.children) {
                    val item = child.getValue(Item::class.java)
                    item?.let {
                        itemList.add(it)
                        itemNames.add(it.name)
                    }
                }

                itemAdapter = ArrayAdapter(
                    this@AddStockOutActivity,
                    android.R.layout.simple_spinner_item,
                    itemNames
                )
                itemAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spItem.adapter = itemAdapter

                binding.spItem.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        selectedItem = itemList[position]
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {
                        selectedItem = null
                    }
                }

                if (itemList.isEmpty()) {
                    Toast.makeText(this@AddStockOutActivity, "Item tidak ditemukan", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddStockOutActivity, "Gagal memuat item: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showDatePicker() {
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(this, { _, y, m, d ->
            calendar.set(y, m, d)
            val format = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            binding.etDate.setText(format.format(calendar.time))
        }, year, month, day)

        datePicker.show()
    }
}
