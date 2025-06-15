package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid

        if (uid == null) {
            Toast.makeText(this, "Belum login, keluar...", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val name = snapshot.child("fullName").getValue(String::class.java) ?: "User"
                b.tvFullName.text = name

                val balance = snapshot.child("balance").getValue(Long::class.java) ?: 0L
                b.tvBalance.text = "IDR %,d".format(balance)

                val stockouts = snapshot.child("stockouts")
                var totalStockoutValue = 0L
                for (stockoutSnapshot in stockouts.children) {
                    val totalPrice = stockoutSnapshot.child("totalPrice").getValue(Long::class.java) ?: 0L
                    totalStockoutValue += totalPrice
                }
                b.tvStockValue.text = "IDR %,d".format(totalStockoutValue)

                val items = snapshot.child("items")
                var totalQty = 0
                for (itemSnapshot in items.children) {
                    val stock = itemSnapshot.child("stock").getValue(Int::class.java) ?: 0
                    totalQty += stock
                }
                b.tvItemsInStock.text = "%,d".format(totalQty)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, "Gagal memuat data: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }

        userRef.addValueEventListener(listener!!)

        // Tombol navigasi
        b.btnAddItem.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }

        b.btnManageItems.setOnClickListener {
            startActivity(Intent(this, ManageItemsActivity::class.java))
        }

        b.btnIncome.setOnClickListener {
            startActivity(Intent(this, ManageFinancialsActivity::class.java))
        }

        b.btnExpenses.setOnClickListener {
            startActivity(Intent(this, ManageExpensesActivity::class.java))
        }

        b.btnStockout.setOnClickListener {
            startActivity(Intent(this, AddStockOutActivity::class.java))
        }

        b.btnAudit.setOnClickListener {
            startActivity(Intent(this, ManageStockOutActivity::class.java))
        }

        // Bottom Navigation Menu
        b.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    // Sudah di halaman ini
                    true
                }
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // Pastikan icon nav_home dipilih
        b.bottomNavigation.selectedItemId = R.id.nav_home
    }

    override fun onDestroy() {
        listener?.let { userRef.removeEventListener(it) }
        super.onDestroy()
    }
}
