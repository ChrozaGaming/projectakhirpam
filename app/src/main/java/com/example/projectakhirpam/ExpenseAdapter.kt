package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirpam.databinding.ItemExpensesBinding
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private val onDelete: (Expense) -> Unit,
    private val onView  : (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.VH>() {

    private val items = mutableListOf<Expense>()

    /** dipanggil oleh Activity/Fragment untuk meng-update list */
    fun submit(list: List<Expense>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /* ---------------- ViewHolder ---------------- */
    inner class VH(val b: ItemExpensesBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            /* hapus */
            b.btnDelete.setOnClickListener {
                val pos = adapterPosition            // kompatibel versi lama
                if (pos != RecyclerView.NO_POSITION) onDelete(items[pos])
            }
            /* lihat bukti */
            b.btnViewReceipt.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onView(items[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemExpensesBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val e   = items[position]
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))

        holder.b.apply {
            tvTanggal.text   = fmt.format(Date(e.date))
            tvKategori.text  = e.category
            tvJumlah.text    = "IDR %,d".format(e.amount)
            tvMetode.text    = e.paymentMethod
            tvDeskripsi.text = e.description
            tvPenerima.text  = e.recipient
        }
    }
}
