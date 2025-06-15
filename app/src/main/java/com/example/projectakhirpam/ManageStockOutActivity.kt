package com.example.projectakhirpam

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageStockoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageStockOutActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageStockoutBinding
    private lateinit var adapter: StockOutAdapter
    private val stockOutList = mutableListOf<StockOut>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageStockoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        adapter = StockOutAdapter(stockOutList)
        binding.recyclerStockout.layoutManager = LinearLayoutManager(this)
        binding.recyclerStockout.adapter = adapter

        loadStockouts()
    }

    private fun loadStockouts() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("stockouts")

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                stockOutList.clear()
                for (data in snapshot.children) {
                    val item = data.getValue(StockOut::class.java)
                    item?.let { stockOutList.add(it) }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageStockOutActivity, "Gagal memuat data", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
