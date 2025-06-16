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

    /* ------------------------------------------------------------ */
    /* 1. Life-cycle                                               */
    /* ------------------------------------------------------------ */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Firebase auth ---
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: run {
            Toast.makeText(this, "Belum login, keluar…", Toast.LENGTH_SHORT).show()
            finish(); return
        }

        // --- Realtime DB reference ---
        userRef = FirebaseDatabase.getInstance().reference
            .child("users").child(uid)

        // --- Listener user info ---
        listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                binding.tvFullName.text =
                    s.child("fullName").getValue(String::class.java) ?: "User"

                val balance = s.child("balance").getValue(Long::class.java) ?: 0L
                binding.tvBalance.text = "IDR %,d".format(balance)

                // total nilai stock-out
                var totalValue = 0L
                for (so in s.child("stockouts").children) {
                    totalValue += so.child("totalPrice")
                        .getValue(Long::class.java) ?: 0L
                }
                binding.tvStockValue.text = "IDR %,d".format(totalValue)

                // total qty item
                var qty = 0
                for (it in s.child("items").children) {
                    qty += it.child("stock").getValue(Int::class.java) ?: 0
                }
                binding.tvItemsInStock.text = "%,d".format(qty)
            }

            override fun onCancelled(e: DatabaseError) =
                Toast.makeText(this@MainActivity,
                    "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
        userRef.addValueEventListener(listener!!)

        // --- Tombol kartu ---
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

        // --- Bottom-Nav listener ---
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home    -> true             // sudah di sini
                R.id.nav_profile -> {
                    startActivity(Intent(this, ProfileActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    /** Selalu kembalikan highlight ke Beranda ketika activity muncul lagi */
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
