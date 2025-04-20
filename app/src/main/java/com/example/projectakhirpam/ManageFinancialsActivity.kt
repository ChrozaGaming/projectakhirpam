package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projectakhirpam.databinding.ActivityManageFinancialsBinding

class ManageFinancialsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityManageFinancialsBinding
    private lateinit var adapter: FinancialTransactionAdapter
    private lateinit var transactionList: MutableList<FinancialTransactionModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageFinancialsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Tombol kembali
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Dummy data
        transactionList = mutableListOf(
            FinancialTransactionModel(
                id = "1",
                date = "22 Apr 2024",
                category = "Gaji",
                amount = "Rp 10.000.000",
                paymentMethod = "Transfer Bank"
            ),
            FinancialTransactionModel(
                id = "2",
                date = "23 Apr 2024",
                category = "Donasi",
                amount = "Rp 500.000",
                paymentMethod = "Tunai"
            )
        )

        // Setup RecyclerView
        adapter = FinancialTransactionAdapter(transactionList) { transaction ->
            transactionList.remove(transaction)
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Transaksi dihapus", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerViewTransactions.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewTransactions.adapter = adapter

        // Sinkronisasi scroll kolom dan data
        setupScrollSync()

        // Aksi tombol tambah
        binding.fabAddTransaction.setOnClickListener {
            val intent = Intent(this, AddIncomesActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupScrollSync() {
        val headerScrollView = binding.headerScrollView
        val contentScrollView = binding.contentScrollView

        // Sinkronisasi scroll horizontal
        headerScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            contentScrollView.scrollTo(scrollX, 0)
        }

        contentScrollView.setOnScrollChangeListener { _, scrollX, _, _, _ ->
            headerScrollView.scrollTo(scrollX, 0)
        }
    }
}
