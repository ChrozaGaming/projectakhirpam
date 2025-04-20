package com.example.projectakhirpam

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// Data class untuk satu entri pengeluaran
data class Expense(
    val tanggal: String,
    val kategori: String,
    val jumlah: String,
    val metodePembayaran: String,
    val deskripsi: String,
    val penerima: String,
    val buktiUrl: String
)

class ManageExpensesActivity : AppCompatActivity() {

    private lateinit var expenseAdapter: ExpenseAdapter
    private val expenseList = mutableListOf(
        Expense("12 Apr 2025", "Makanan", "IDR 75.000", "E-wallet", "Lunch bareng tim", "Warung A", "bukti1.jpg"),
        Expense("13 Apr 2025", "Transportasi", "IDR 20.000", "Tunai", "Naik ojek", "Pak Budi", "bukti2.jpg")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_expenses)

        // Inisialisasi RecyclerView
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_expenses)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Inisialisasi Adapter
        expenseAdapter = ExpenseAdapter(expenseList,
            onViewClick = { expense ->
                Toast.makeText(this, "Lihat bukti: ${expense.buktiUrl}", Toast.LENGTH_SHORT).show()
            },
            onDeleteClick = { expense ->
                expenseList.remove(expense)
                expenseAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Pengeluaran dihapus", Toast.LENGTH_SHORT).show()
            }
        )
        recyclerView.adapter = expenseAdapter

        // Tombol kembali ke activity sebelumnya
        val btnBack = findViewById<ImageButton>(R.id.btn_back)
        btnBack.setOnClickListener {
            finish()
        }

        // FAB ke halaman tambah pengeluaran
        val fabAdd = findViewById<View>(R.id.fab_add)
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddExpensesActivity::class.java)
            startActivity(intent)
        }
    }

    // Adapter RecyclerView internal
    class ExpenseAdapter(
        private val expenses: List<Expense>,
        private val onViewClick: (Expense) -> Unit,
        private val onDeleteClick: (Expense) -> Unit
    ) : RecyclerView.Adapter<ExpenseAdapter.ExpenseViewHolder>() {

        inner class ExpenseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvTanggal: TextView = view.findViewById(R.id.tv_tanggal)
            val tvKategori: TextView = view.findViewById(R.id.tv_kategori)
            val tvJumlah: TextView = view.findViewById(R.id.tv_jumlah)
            val tvMetode: TextView = view.findViewById(R.id.tv_metode)
            val tvDeskripsi: TextView = view.findViewById(R.id.tv_deskripsi)
            val tvPenerima: TextView = view.findViewById(R.id.tv_penerima)
            val btnView: ImageButton = view.findViewById(R.id.btn_view_receipt)
            val btnDelete: ImageButton = view.findViewById(R.id.btn_delete)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpenseViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_expenses, parent, false) // ✅ gunakan layout item_expenses.xml
            return ExpenseViewHolder(view)
        }

        override fun onBindViewHolder(holder: ExpenseViewHolder, position: Int) {
            val expense = expenses[position]
            holder.tvTanggal.text = expense.tanggal
            holder.tvKategori.text = expense.kategori
            holder.tvJumlah.text = expense.jumlah
            holder.tvMetode.text = expense.metodePembayaran
            holder.tvDeskripsi.text = expense.deskripsi
            holder.tvPenerima.text = expense.penerima

            holder.btnView.setOnClickListener { onViewClick(expense) }
            holder.btnDelete.setOnClickListener { onDeleteClick(expense) }
        }

        override fun getItemCount(): Int = expenses.size
    }
}
