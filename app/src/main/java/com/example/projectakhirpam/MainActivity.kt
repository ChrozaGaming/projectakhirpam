package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var b       : ActivityMainBinding
    private lateinit var auth    : FirebaseAuth
    private lateinit var userRef : DatabaseReference
    private var listener         : ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* ---------- MUAT PROFIL USER ---------- */
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid
        if (uid == null) {                 // user belum login → keluar
            finish(); return
        }

        userRef = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid)

        listener = object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                /* nama */
                val name = s.child("fullName").getValue(String::class.java) ?: "User"
                b.tvFullName.text = name

                /* saldo – default 0 jika field belum ada */
                val balance = s.child("balance").getValue(Long::class.java) ?: 0L
                b.tvBalance.text = "IDR %,d".format(balance)
            }
            override fun onCancelled(e: DatabaseError) {
                Toast.makeText(this@MainActivity,
                    "Gagal memuat data: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        userRef.addValueEventListener(listener!!)

        /* ---------- TOMBOL NAVIGASI ---------- */
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
    }

    override fun onDestroy() {
        listener?.let { userRef.removeEventListener(it) }
        super.onDestroy()
    }
}
