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

        /* ---------- ambil nama ---------- */
        auth = FirebaseAuth.getInstance()
        auth.currentUser?.uid?.let { uid ->
            userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)

            listener = object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val fullName = snapshot.child("fullName").getValue(String::class.java)
                    binding.tvUsername.text = fullName ?: "User"
                }
                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity,
                        "Gagal memuat nama: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            }
            userRef.addListenerForSingleValueEvent(listener!!)
        }

        /* ---------- tombol navigasi ---------- */
        binding.apply {
            btnManageItems.setOnClickListener {
                startActivity(Intent(this@MainActivity, ManageItemsActivity::class.java))
            }
            btnAddItem.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddItemActivity::class.java))
            }
            btnIncome.setOnClickListener {
                startActivity(Intent(this@MainActivity, ManageFinancialsActivity::class.java))
            }
            btnExpenses.setOnClickListener {
                startActivity(Intent(this@MainActivity, ManageExpensesActivity::class.java))
            }
        }
    }

    override fun onDestroy() {
        listener?.let { userRef.removeEventListener(it) }
        super.onDestroy()
    }
}
