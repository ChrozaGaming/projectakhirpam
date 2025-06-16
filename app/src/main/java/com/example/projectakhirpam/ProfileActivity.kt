package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var userRef: DatabaseReference
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /* ---------- Toolbar back ---------- */
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        /* ---------- Firebase ---------- */
        auth = FirebaseAuth.getInstance()
        val uid = auth.currentUser?.uid ?: run { finish(); return }

        userRef = FirebaseDatabase.getInstance()
            .getReference("users").child(uid)

        listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                binding.tvFullName.text =
                    snapshot.child("fullName").getValue(String::class.java) ?: "User"
                binding.tvEmail.text =
                    snapshot.child("email").getValue(String::class.java) ?: "email@example.com"
            }
            override fun onCancelled(error: DatabaseError) { /* log if needed */ }
        }
        userRef.addValueEventListener(listener!!)

        /* ---------- Edit profile ---------- */
        binding.btnEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        /* ---------- LOGOUT ---------- */
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            // Kembali ke layar login & bersihkan back-stack
            val i = Intent(this, LoginActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            finish()
        }

        /* ---------- Bottom Navigation ---------- */
        val nav = binding.bottomNavigation
        nav.menu.findItem(R.id.nav_profile).isChecked = true

        nav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    startActivity(Intent(this, MainActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                Intent.FLAG_ACTIVITY_SINGLE_TOP))
                    finish()
                    true
                }
                R.id.nav_profile -> true
                else -> false
            }
        }
    }

    override fun onDestroy() {
        listener?.let { userRef.removeEventListener(it) }
        super.onDestroy()
    }
}
