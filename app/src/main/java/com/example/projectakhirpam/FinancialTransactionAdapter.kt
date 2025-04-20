package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class FinancialTransactionAdapter(
    private val transactionList: List<FinancialTransactionModel>,
    private val onDeleteClick: (FinancialTransactionModel) -> Unit
) : RecyclerView.Adapter<FinancialTransactionAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val tvPaymentMethod: TextView = view.findViewById(R.id.tvPaymentMethod)
        val ivDelete: ImageView = view.findViewById(R.id.ivDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = transactionList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = transactionList[position]
        holder.tvDate.text = item.date
        holder.tvCategory.text = item.category
        holder.tvAmount.text = item.amount
        holder.tvPaymentMethod.text = item.paymentMethod

        holder.ivDelete.setOnClickListener {
            onDeleteClick(item)
        }
    }
}
