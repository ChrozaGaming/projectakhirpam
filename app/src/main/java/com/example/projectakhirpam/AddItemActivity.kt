package com.example.projectakhirpam

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityAddItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.NumberFormat
import java.util.*

class AddItemActivity : AppCompatActivity() {

    private lateinit var b: ActivityAddItemsBinding
    private val nf = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityAddItemsBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* ← back / batal */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }
        b.btnCancel.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* live preview harga */
        b.etPrice.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val num = s?.toString()?.filter(Char::isDigit)?.toLongOrNull() ?: 0L
                b.tvPriceDisplay.text = nf.format(num)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, c: Int, a: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, b: Int, c: Int) {}
        })

        /* simpan */
        b.btnSave.setOnClickListener { saveItem() }
    }

    private fun saveItem() {
        val name  = b.etName.text.toString().trim()
        val stock = b.etStock.text.toString().trim().toIntOrNull() ?: -1
        val code  = b.etCode.text.toString().trim()
        val price = b.etPrice.text.toString().filter(Char::isDigit).toLongOrNull() ?: -1

        if (name.isEmpty() || code.isEmpty() || stock < 0 || price <= 0) {
            Toast.makeText(this, "Lengkapi data dengan benar", Toast.LENGTH_SHORT).show(); return
        }

        val uid      = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val itemsRef = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("items")

        val key  = itemsRef.push().key ?: return
        val item = Item(key, name, stock, code, price)

        b.btnSave.isEnabled = false
        itemsRef.child(key).setValue(item)
            .addOnSuccessListener {
                Toast.makeText(this, "Item tersimpan", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener {
                b.btnSave.isEnabled = true
                Toast.makeText(this, "Gagal: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}
