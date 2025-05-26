package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirpam.databinding.ItemInventoryBinding
import java.text.NumberFormat
import java.util.*

class ItemAdapter(
    private val onDelete: (Item) -> Unit,
    private val onEdit  : (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    /** data lokal */
    private val list = mutableListOf<Item>()

    /** helper format rupiah */
    private val nf = NumberFormat.getCurrencyInstance(Locale("in", "ID"))

    /** ganti seluruh isi adapter */
    fun submit(newData: List<Item>) {
        list.clear()
        list.addAll(newData)
        notifyDataSetChanged()
    }

    /* ---------------- ViewHolder ---------------- */
    inner class VH(val b: ItemInventoryBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.btnDelete.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onDelete(list[pos])
            }
            b.btnEdit.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) onEdit(list[pos])
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH =
        VH(ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: VH, position: Int) = with(holder.b) {
        val item = list[position]
        tvNamaBarang.text  = item.name
        tvJumlahStok.text  = item.stock.toString()
        tvKodeBarang.text  = item.code
        tvHarga.text       = nf.format(item.price)
    }
}
