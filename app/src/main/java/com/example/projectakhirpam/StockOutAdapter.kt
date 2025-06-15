package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.*

class StockOutAdapter(private val stockOutList: List<StockOut>) :
    RecyclerView.Adapter<StockOutAdapter.StockOutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StockOutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stockout, parent, false)
        return StockOutViewHolder(view)
    }

    override fun onBindViewHolder(holder: StockOutViewHolder, position: Int) {
        val stockOut = stockOutList[position]
        holder.bind(stockOut)
    }

    override fun getItemCount(): Int = stockOutList.size

    inner class StockOutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvItemName = itemView.findViewById<TextView>(R.id.tv_item_name)
        private val tvQty = itemView.findViewById<TextView>(R.id.tv_qty)
        private val tvUnitPrice = itemView.findViewById<TextView>(R.id.tv_unit_price)
        private val tvTotalPrice = itemView.findViewById<TextView>(R.id.tv_total_price)
        private val tvDestination = itemView.findViewById<TextView>(R.id.tv_destination)
        private val tvDate = itemView.findViewById<TextView>(R.id.tv_date)

        fun bind(data: StockOut) {
            val nf = NumberFormat.getCurrencyInstance(Locale("in", "ID"))
            tvItemName.text = data.itemName
            tvQty.text = data.qty.toString()
            tvUnitPrice.text = nf.format(data.unitPrice)
            tvTotalPrice.text = nf.format(data.totalPrice)
            tvDestination.text = data.destination
            tvDate.text = data.date
        }
    }
}
