package com.example.projectakhirpam

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageStockoutBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageStockOutActivity : AppCompatActivity() {

    private lateinit var b: ActivityManageStockoutBinding
    private val list = mutableListOf<StockOut>()
    private lateinit var adapter: StockOutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityManageStockoutBinding.inflate(layoutInflater)
        setContentView(b.root)

        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        adapter = StockOutAdapter(list)
        b.recyclerStockout.layoutManager = LinearLayoutManager(this)
        b.recyclerStockout.adapter       = adapter

        listenData()
    }

    private fun listenData(){
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        FirebaseDatabase.getInstance().reference
            .child("users").child(uid).child("stockouts")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(s: DataSnapshot) {
                    list.clear()
                    for (c in s.children){
                        c.getValue(StockOut::class.java)?.let { list += it }
                    }
                    adapter.notifyDataSetChanged()
                }
                override fun onCancelled(e: DatabaseError) {
                    Toast.makeText(this@ManageStockOutActivity,
                        "Gagal: ${e.message}",Toast.LENGTH_SHORT).show()
                }
            })
    }
}
