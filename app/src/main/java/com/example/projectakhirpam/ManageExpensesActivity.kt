package com.example.projectakhirpam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageExpensesBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageExpensesActivity : AppCompatActivity() {

    private lateinit var b       : ActivityManageExpensesBinding
    private lateinit var adapter : ExpenseAdapter
    private lateinit var refExp  : DatabaseReference
    private var listener         : ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityManageExpensesBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* tombol back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* path  /users/{uid}/expenses */
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        refExp  = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("expenses")

        /* RecyclerView */
        adapter = ExpenseAdapter(
            onDelete = { exp -> deleteExpense(exp) },
            onView   = { exp ->
                if (exp.receiptUrl.isNotEmpty()) {
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(exp.receiptUrl)))
                } else {
                    Toast.makeText(this, "Tidak ada bukti", Toast.LENGTH_SHORT).show()
                }
            }
        )
        b.recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        b.recyclerViewExpenses.adapter       = adapter

        /* FAB → form tambah */
        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExpensesActivity::class.java))
        }

        syncHeader()      // sinkron scroll header & isi
    }

    /* -------------------- Real-time listener -------------------- */
    override fun onStart() {
        super.onStart()
        listener = refExp.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snap: DataSnapshot) {
                val list = snap.children
                    .mapNotNull { it.getValue(Expense::class.java) }
                    .sortedByDescending { it.date }
                adapter.submit(list)
            }
            override fun onCancelled(error: DatabaseError) { /* no-op */ }
        })
    }
    override fun onStop() {
        super.onStop()
        listener?.let { refExp.removeEventListener(it) }
    }

    /* -------------------- Hapus + perbaiki saldo -------------------- */
    private fun deleteExpense(exp: Expense) {
        refExp.child(exp.id).removeValue()
            .addOnSuccessListener {
                updateBalance(+exp.amount)   // hapus pengeluaran → saldo naik
                Toast.makeText(this, "Dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    /** balance naik, totalExpense turun */
    private fun updateBalance(delta: Long) {
        val uid      = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef  = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        fun change(node: String, plus: Boolean) {
            userRef.child(node).runTransaction(object : Transaction.Handler {
                override fun doTransaction(cur: MutableData): Transaction.Result {
                    val now = cur.getValue(Long::class.java) ?: 0L
                    cur.value = if (plus) now + delta else now - delta
                    return Transaction.success(cur)
                }
                override fun onComplete(e: DatabaseError?, committed: Boolean, snap: DataSnapshot?) {}
            })
        }
        change("balance",      true)   // saldo + delta
        change("totalExpense", false)  // totalExpense - delta
    }

    /* -------------------- Sinkron scroll header / body -------------------- */
    private fun syncHeader() {
        /* geser header → geser isi */
        b.headerScrollView.setOnScrollChangeListener { _, x, _, _, _ ->
            b.contentScrollView.scrollTo(x, 0)
        }
        /* geser isi → geser header */
        b.contentScrollView.setOnScrollChangeListener { _, x, _, _, _ ->
            b.headerScrollView.scrollTo(x, 0)
        }
    }
}
