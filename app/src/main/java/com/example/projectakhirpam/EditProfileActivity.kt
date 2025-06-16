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

        /* ---------- Firebase init ---------- */
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: return finish()
        userRef = FirebaseDatabase.getInstance().reference
            .child("users")
            .child(uid)

        /* ---------- Muat data awal ---------- */
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                binding.etFullName.setText(
                    s.child("fullName").getValue(String::class.java) ?: ""
                )
                // Jika tetap ingin menampilkan e-mail, bisa pakai Toast/log di sini
                // val email = s.child("email").getValue(String::class.java)
            }
            override fun onCancelled(e: DatabaseError) {
                Toast.makeText(
                    this@EditProfileActivity,
                    e.toException().toUserMessage(),
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        /* tombol */
        binding.btnSave.setOnClickListener { saveNameOnly() }
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    /** Simpan hanya Nama ke Realtime DB */
    private fun saveNameOnly() {
        val newName = binding.etFullName.text.toString().trim()
        if (newName.isEmpty()) {
            Toast.makeText(this, "Nama tidak boleh kosong", Toast.LENGTH_SHORT).show()
            return
        }

        userRef.child("fullName").setValue(newName)
            .addOnSuccessListener {
                Toast.makeText(this, "Nama diperbarui", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.toUserMessage(), Toast.LENGTH_LONG).show()
            }
    }
}
