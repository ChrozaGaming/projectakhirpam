package com.example.projectakhirpam

import android.content.Intent // ✅ tambahkan ini
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageItemsBinding


class ManageItemsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageItemsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageItemsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol back fungsional
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Dummy Data
        val itemList = listOf(
            ItemModel("Barang A", 10, "KDA001", "IDR 50.000"),
            ItemModel("Barang B", 20, "KDA002", "IDR 75.000"),
            ItemModel("Barang C", 30, "KDA003", "IDR 100.000.000"),
            ItemModel("Barang D", 5, "KDA004", "IDR 25.000")
        )

        // Setup RecyclerView
        val adapter = ItemAdapter(itemList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // FAB Add Item
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
    }
}
