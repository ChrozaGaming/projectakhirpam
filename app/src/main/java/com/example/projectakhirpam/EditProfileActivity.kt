package com.example.projectakhirpam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class EditProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return finish()

        userRef = FirebaseDatabase.getInstance().getReference("users").child(uid)

        // Load data awal ke input
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.etFullName.setText(snapshot.child("fullName").getValue(String::class.java))
                binding.etEmail.setText(snapshot.child("email").getValue(String::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@EditProfileActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })

        // Tombol Simpan
        binding.btnSave.setOnClickListener {
            val newName = binding.etFullName.text.toString().trim()
            val newEmail = binding.etEmail.text.toString().trim()

            if (newName.isEmpty() || newEmail.isEmpty()) {
                Toast.makeText(this, "Nama dan email tidak boleh kosong", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val updates = mapOf(
                "fullName" to newName,
                "email" to newEmail
            )

            userRef.updateChildren(updates).addOnSuccessListener {
                Toast.makeText(this, "Profil diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Gagal menyimpan: ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }

        // Tombol kembali
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }
}
