package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirpam.databinding.ItemTransactionBinding
import java.text.SimpleDateFormat
import java.util.*

class IncomeAdapter(
    private val onDelete: (Income) -> Unit
) : RecyclerView.Adapter<IncomeAdapter.IncomeVH>() {

    private val items = mutableListOf<Income>()

    fun submit(list: List<Income>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    /* ------------ ViewHolder ------------ */
    inner class IncomeVH(val b: ItemTransactionBinding) : RecyclerView.ViewHolder(b.root) {
        init {
            b.btnDelete.setOnClickListener {
                val pos = adapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    onDelete(items[pos])
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncomeVH =
        IncomeVH(
            ItemTransactionBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: IncomeVH, position: Int) {
        val inc = items[position]
        val fmt = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
        holder.b.tvDate.text     = fmt.format(Date(inc.date))
        holder.b.tvCategory.text = inc.category
        holder.b.tvAmount.text   = "IDR %,d".format(inc.amount)
        holder.b.tvPayment.text  = inc.paymentMethod
    }
}
