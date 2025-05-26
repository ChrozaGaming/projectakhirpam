package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageItemsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ManageItemsActivity : AppCompatActivity() {

    private lateinit var b       : ActivityManageItemsBinding
    private lateinit var adapter : ItemAdapter
    private lateinit var refItem : DatabaseReference
    private var listener : ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityManageItemsBinding.inflate(layoutInflater)
        setContentView(b.root)

        /* ← back */
        b.btnBack.setOnClickListener { onBackPressedDispatcher.onBackPressed() }

        /* path /users/{uid}/items */
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        refItem = FirebaseDatabase.getInstance()
            .reference.child("users").child(uid).child("items")

        /* Recycler */
        adapter = ItemAdapter(
            onDelete = { item -> deleteItem(item) },
            onEdit   = { item  ->
                // Contoh simpel: buka AddItemActivity dengan extra untuk edit,
                // (belum di-implement detail; bisa Anda kembangkan sendiri)
                val i = Intent(this, AddItemActivity::class.java)
                i.putExtra("EDIT_ID", item.id)
                startActivity(i)
            }
        )
        b.recyclerView.layoutManager = LinearLayoutManager(this)
        b.recyclerView.adapter       = adapter

        /* FAB tambah */
        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, AddItemActivity::class.java))
        }
    }

    /* realtime listener */
    override fun onStart() {
        super.onStart()
        listener = refItem.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(s: DataSnapshot) {
                val list = s.children.mapNotNull { it.getValue(Item::class.java) }
                adapter.submit(list)
            }
            override fun onCancelled(e: DatabaseError) { }
        })
    }
    override fun onStop() {
        super.onStop(); listener?.let { refItem.removeEventListener(it) }
    }

    /* hapus item */
    private fun deleteItem(item: Item) {
        refItem.child(item.id).removeValue()
            .addOnSuccessListener {
                Toast.makeText(this, "Item dihapus", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Gagal hapus: ${it.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }
}
