package com.example.projectakhirpam

import android.content.Intent
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

        /* ← back */
        b.btnBack.setOnClickListener { finish() }

        /* Firebase path /users/{uid}/expenses */
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        refExp  = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("expenses")

        /* Recycler */
        adapter = ExpenseAdapter { exp -> deleteExpense(exp) }
        b.recyclerViewExpenses.layoutManager = LinearLayoutManager(this)
        b.recyclerViewExpenses.adapter       = adapter

        /* FAB → form tambah */
        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddExpensesActivity::class.java))
        }

        syncHeader()          // sinkronkan scroll header–isi
    }

    /* ---------- realtime list ---------- */
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

    /* ---------- hapus + saldo ---------- */
    private fun deleteExpense(exp: Expense) {
        refExp.child(exp.id).removeValue()
            .addOnSuccessListener {
                updateBalance(+exp.amount)            // pengeluaran dihapus → saldo ↑
                Toast.makeText(this, "Dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    /** balance ↑, totalExpense ↓ */
    private fun updateBalance(delta: Long) {
        val uid     = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        fun mutate(node: String, plus: Boolean) = userRef.child(node)
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(cur: MutableData): Transaction.Result {
                    val now = cur.getValue(Long::class.java) ?: 0L
                    cur.value = if (plus) now + delta else now - delta
                    return Transaction.success(cur)
                }
                override fun onComplete(
                    error: DatabaseError?, committed: Boolean, snapshot: DataSnapshot?
                ) { /* nothing */ }
            })

        mutate("balance",       true)   // saldo naik
        mutate("totalExpense",  false)  // totalExpense turun
    }

    /* ---------- header/isi scroll sync ---------- */
    private fun syncHeader() {
        b.headerScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            b.contentScrollView.scrollTo(scrollX, 0)
        }
        b.contentScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            b.headerScrollView.scrollTo(scrollX, 0)
        }
    }
}