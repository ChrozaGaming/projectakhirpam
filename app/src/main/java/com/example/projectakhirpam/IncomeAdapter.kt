package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.projectakhirpam.databinding.DialogReceiptPreviewBinding
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

    /* ---------------- View-holder ---------------- */
    inner class IncomeVH(val b: ItemTransactionBinding) :
        RecyclerView.ViewHolder(b.root) {

        init {
            /* hapus */
            b.btnDelete.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) onDelete(items[pos])
            }
            /* lihat bukti */
            b.ivViewReceipt.setOnClickListener {
                val pos = bindingAdapterPosition
                if (pos != RecyclerView.NO_POSITION) {
                    items[pos].receiptUrl?.takeIf { it.isNotBlank() }?.let { url ->
                        showPreview(url)
                    }
                }
            }
        }

        fun bind(inc: Income) = with(b) {
            val fmt = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))
            tvDate.text     = fmt.format(Date(inc.date))
            tvCategory.text = inc.category
            tvAmount.text   = "IDR %,d".format(inc.amount)
            tvPayment.text  = inc.paymentMethod

            ivViewReceipt.visibility =
                if (inc.receiptUrl.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        /* -------- full-screen preview dialog -------- */
        private fun showPreview(url: String) {
            val ctx = b.root.context
            val binding = DialogReceiptPreviewBinding
                .inflate(LayoutInflater.from(ctx))

            val dialog = AlertDialog
                .Builder(ctx, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                .setView(binding.root)
                .create()

            binding.ivReceiptBig.load(url) {
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_broken_image)
            }
            binding.btnClose.setOnClickListener { dialog.dismiss() }
            dialog.show()
        }
    }

    /* ---------------- Adapter std ---------------- */
    override fun onCreateViewHolder(p: ViewGroup, t: Int) =
        IncomeVH(ItemTransactionBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: IncomeVH, p: Int) = h.bind(items[p])

    override fun getItemCount(): Int = items.size
}
