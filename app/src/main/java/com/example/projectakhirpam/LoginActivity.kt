package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {

    private lateinit var b   : ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private val db           = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(b.root)

        auth = FirebaseAuth.getInstance()

        /* -------- tombol -------- */
        b.btnEmailLogin.setOnClickListener { loginWithEmail() }
        b.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    /* ------------------------------------------------------------ */
    /*                           LOGIN                              */
    /* ------------------------------------------------------------ */
    private fun loginWithEmail() {
        val email = b.etEmail.text.toString().trim()
        val pass  = b.etPassword.text.toString().trim()

        if (email.isEmpty() || pass.isEmpty()) {
            Toast.makeText(this, "Masukkan email & password!", Toast.LENGTH_SHORT).show()
            return
        }

        showLoading(true)

        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                ensureUserNode {          // ← tambahkan user ke Realtime DB jika belum ada
                    showLoading(false)
                    Toast.makeText(this, "Login berhasil", Toast.LENGTH_SHORT).show()
                    navigateToMain()
                }
            } else {
                showLoading(false)
                Toast.makeText(
                    this,
                    "Login gagal: ${task.exception?.localizedMessage}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    /* ------------------------------------------------------------ */
    /*      Pastikan /users/{uid} ada.  Dipanggil setelah login     */
    /* ------------------------------------------------------------ */
    private fun ensureUserNode(onDone: () -> Unit) {
        val curUser = auth.currentUser ?: return onDone()

        val userNode = db.child("users").child(curUser.uid)
        userNode.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                if (snap.exists()) {
                    // Profil sudah ada → langsung lanjut
                    onDone()
                } else {
                    // Buat profil dasar (fullName bisa diambil dari displayName, jika ada)
                    val user = User(
                        uid      = curUser.uid,
                        fullName = curUser.displayName ?: "",
                        email    = curUser.email ?: "",
                        balance  = 0L,
                        totalIncome  = 0L,
                        totalExpense = 0L
                    )
                    userNode.setValue(user)
                        .addOnCompleteListener { onDone() }    // lanjut meski gagal/berhasil
                }
            }
            override fun onCancelled(error: DatabaseError) = onDone()
        })
    }

    /* ------------------------------------------------------------ */
    /*                     util tampilan / helper                   */
    /* ------------------------------------------------------------ */
    private fun navigateToMain() {
        startActivity(
            Intent(this, MainActivity::class.java)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        )
        finish()
    }

    private fun showLoading(state: Boolean) {
        b.progressBar.visibility  = if (state) View.VISIBLE else View.GONE
        b.btnEmailLogin.isEnabled = !state
        b.tvRegister.isEnabled    = !state
    }
}
