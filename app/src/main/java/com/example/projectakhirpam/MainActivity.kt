package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Belum login, keluar…", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(uid)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (!snapshot.exists()) return

                binding.tvFullName.text =
                    snapshot.child("fullName").getValue(String::class.java) ?: "Anonim"

                val balance = snapshot.child("balance").getValue(Long::class.java) ?: 0L
                binding.tvBalance.text = "IDR %,d".format(balance)

                var totalValue = 0L
                snapshot.child("stockouts").children.forEach { so ->
                    totalValue += so.child("totalPrice").getValue(Long::class.java) ?: 0L
                }
                binding.tvStockValue.text = "IDR %,d".format(totalValue)

                var qty = 0
                snapshot.child("items").children.forEach { item ->
                    qty += item.child("stock").getValue(Int::class.java) ?: 0
                }
                binding.tvItemsInStock.text = "%,d".format(qty)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@MainActivity,
                    "Gagal memuat data: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        userRef.addValueEventListener(listener!!)

        /* Tombol kartu */
        binding.btnAddItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
        binding.btnManageItems.setOnClickListener {
            startActivity(Intent(this, ManageItemsActivity::class.java))
        }
        binding.btnIncome.setOnClickListener {
            startActivity(Intent(this, ManageFinancialsActivity::class.java))
        }
        binding.btnExpenses.setOnClickListener {
            startActivity(Intent(this, ManageExpensesActivity::class.java))
        }
        binding.btnStockout.setOnClickListener {
            startActivity(Intent(this, AddStockOutActivity::class.java))
        }
        binding.btnAudit.setOnClickListener {
            startActivity(Intent(this, ManageStockOutActivity::class.java))
        }

        /* Bottom navigation */
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> true
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.bottomNavigation.menu
            .findItem(R.id.nav_home).isChecked = true
    }

    override fun onDestroy() {
        listener?.let { userRef.removeEventListener(it) }
        super.onDestroy()
    }
}
