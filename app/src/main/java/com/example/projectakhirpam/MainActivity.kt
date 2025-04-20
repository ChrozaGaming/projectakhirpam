package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projectakhirpam.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol ke halaman Manage Items
        binding.btnManageItems.setOnClickListener {
            val intent = Intent(this, ManageItemsActivity::class.java)
            startActivity(intent)
        }

        // Tombol ke halaman Add Item
        binding.btnAddItem.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }

        // ✅ Pindah ke halaman Add Incomes
        binding.btnIncome.setOnClickListener {
            startActivity(Intent(this, ManageFinancialsActivity::class.java))
        }

        // ✅ Pindah ke halaman Add Expenses
        binding.btnExpenses.setOnClickListener {
            val intent = Intent(this, ManageExpensesActivity::class.java)
            startActivity(intent)
        }
    }
}
