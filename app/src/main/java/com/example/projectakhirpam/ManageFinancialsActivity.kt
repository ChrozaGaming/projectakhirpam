package com.example.projectakhirpam          // ← tetap

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageFinancialsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

// ───── GANTI baris ini ─────
// import com.example.projectakhirpam.model.Income
// ───── MENJADI ─────
import com.example.projectakhirpam.Income
// atau tulis path sesuai lokasi asli file Income.kt

class ManageFinancialsActivity : AppCompatActivity() {

    private lateinit var b       : ActivityManageFinancialsBinding
    private lateinit var adapter : IncomeAdapter
    private lateinit var refIn   : DatabaseReference
    private var listener : ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityManageFinancialsBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* tombol back & tambah */
        b.btnBack.setOnClickListener { finish() }
        b.fabAddTransaction.setOnClickListener {
            startActivity(Intent(this, AddIncomesActivity::class.java))
        }

        /* RecyclerView */
        adapter = IncomeAdapter { deleteIncome(it) }
        b.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        b.recyclerViewTransactions.adapter       = adapter

        /* Firebase reference  users/{uid}/incomes */
        val uid  = FirebaseAuth.getInstance().currentUser?.uid ?: return
        refIn    = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("incomes")

        syncHeader()
    }

    /* realtime listener */
    override fun onStart() {
        super.onStart()
        listener = refIn.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(ss: DataSnapshot) {
                val list = ss.children
                    .mapNotNull { it.getValue(Income::class.java) }
                    .sortedByDescending { it.date }
                adapter.submit(list)
            }
            override fun onCancelled(e: DatabaseError) {}
        })
    }

    override fun onStop() {
        super.onStop()
        listener?.let { refIn.removeEventListener(it) }
    }

    /* hapus & sesuaikan saldo */
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
        val uid   = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val uRef  = FirebaseDatabase.getInstance().reference.child("users").child(uid)

        fun incField(field: String) = uRef.child(field)
            .runTransaction(object : Transaction.Handler {
                override fun doTransaction(d: MutableData): Transaction.Result {
                    d.value = (d.getValue(Long::class.java) ?: 0L) + delta
                    return Transaction.success(d)
                }
                override fun onComplete(e: DatabaseError?, c: Boolean, s: DataSnapshot?) {}
            })

        incField("balance")
        incField("totalIncome")
    }

    /* sinkronisasi header–konten */
    private fun syncHeader() {
        b.headerScrollView.setOnScrollChangeListener { _, x, _, _, _ ->
            b.contentScrollView.scrollTo(x, 0)
        }
        b.contentScrollView.setOnScrollChangeListener { _, x, _, _, _ ->
            b.headerScrollView.scrollTo(x, 0)
        }
    }
}
