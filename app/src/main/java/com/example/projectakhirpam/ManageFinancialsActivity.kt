package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageFinancialsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageFinancialsActivity : AppCompatActivity() {

    private lateinit var b: ActivityManageFinancialsBinding
    private lateinit var adapter: IncomeAdapter
    private lateinit var refIn: DatabaseReference
    private var listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityManageFinancialsBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* tombol back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* path /users/{uid}/incomes */
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        refIn = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("incomes")

        /* recycler */
        adapter = IncomeAdapter { inc -> deleteIncome(inc) }
        b.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        b.recyclerViewTransactions.adapter = adapter

        /* FAB → AddIncomesActivity */
        b.fabAddTransaction.setOnClickListener {
            val i = Intent(this@ManageFinancialsActivity, AddIncomesActivity::class.java)
            startActivity(i)
        }

        syncHeader()
    }

    /* ----- Realtime listener ----- */
    override fun onStart() {
        super.onStart()
        listener = refIn.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = snapshot.children
                    .mapNotNull { it.getValue(Income::class.java) }
                    .sortedByDescending { it.date }
                adapter.submit(list)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    override fun onStop() {
        super.onStop()
        listener?.let { refIn.removeEventListener(it) }
    }

    /* ----- Hapus income + update saldo ----- */
    private fun deleteIncome(inc: Income) {
        refIn.child(inc.id).removeValue()
            .addOnSuccessListener {
                updateBalance(-inc.amount)
                Toast.makeText(this, "Dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateBalance(delta: Long) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        fun incNode(child: String) = userRef.child(child)
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(cur: MutableData): Transaction.Result {
                    cur.value = (cur.getValue(Long::class.java) ?: 0L) + delta
                    return Transaction.success(cur)
                }
                override fun onComplete(e: DatabaseError?, c: Boolean, s: DataSnapshot?) {}
            })

        incNode("balance")
        incNode("totalIncome")
    }

    /* sinkron scroll header–isi */
    private fun syncHeader() {
        b.headerScrollView.setOnScrollChangeListener { _, x, _, _, _ ->
            b.contentScrollView.scrollTo(x, 0)
        }
        b.contentScrollView.setOnScrollChangeListener { _, x, _, _, _ ->
            b.headerScrollView.scrollTo(x, 0)
        }
    }
}
