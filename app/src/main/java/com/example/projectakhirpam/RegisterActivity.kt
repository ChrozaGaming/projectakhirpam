package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.btnRegister.setOnClickListener { registerUser() }
        binding.tvLoginLink.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun registerUser() {
        val name  = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val pass  = binding.etPassword.text.toString().trim()

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Lengkapi semua kolom!", Toast.LENGTH_SHORT).show()
            return
        }

        auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { t ->
            if (t.isSuccessful) {
                val uid = t.result?.user?.uid ?: return@addOnCompleteListener
                val user = User(uid, name, email)    // saldo & agregat = 0
                FirebaseDatabase.getInstance()
                    .reference.child("users").child(uid).setValue(user)

                auth.signOut() // paksa login ulang
                Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                startActivity(
                    Intent(this, LoginActivity::class.java)
                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                )
                finish()
            } else {
                Toast.makeText(this,
                    "Gagal registrasi: ${t.exception?.localizedMessage}",
                    Toast.LENGTH_LONG).show()
            }
        }
    }
}
