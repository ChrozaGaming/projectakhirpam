package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.projectakhirpam.databinding.ItemExpensesBinding
import java.text.SimpleDateFormat
import java.util.*

class ExpenseAdapter(
    private val onDelete: (Expense) -> Unit
) : RecyclerView.Adapter<ExpenseAdapter.VH>() {

    private val items = mutableListOf<Expense>()
    private val fmt   = SimpleDateFormat("dd MMM yyyy", Locale("in", "ID"))

    /* ---------- public API ---------- */
    fun submit(newData: List<Expense>) {
        items.clear()
        items.addAll(newData)
        notifyDataSetChanged()
    }

    /* ---------- View-holder ---------- */
    inner class VH(val b: ItemExpensesBinding) : RecyclerView.ViewHolder(b.root) {

        init {
            /* hapus */
            b.btnDelete.setOnClickListener {
                val p = bindingAdapterPosition
                if (p != RecyclerView.NO_POSITION) onDelete(items[p])
            }
            /* lihat bukti */
            b.btnViewReceipt.setOnClickListener {
                val p = bindingAdapterPosition
                if (p != RecyclerView.NO_POSITION) {
                    items[p].receiptUrl
                        .takeIf { it.isNotBlank() }
                        ?.let(::showPreview)
                }
            }
        }

        fun bind(e: Expense) = with(b) {
            tvTanggal.text   = fmt.format(Date(e.date))
            tvKategori.text  = e.category
            tvJumlah.text    = "IDR %,d".format(e.amount)
            tvMetode.text    = e.paymentMethod
            tvDeskripsi.text = e.description
            tvPenerima.text  = e.recipient

            btnViewReceipt.visibility =
                if (e.receiptUrl.isBlank()) View.GONE else View.VISIBLE
        }

        /* --------- popup viewer --------- */
        private fun showPreview(url: String) {
            val ctx  = b.root.context
            val view = LayoutInflater.from(ctx)
                .inflate(R.layout.dialog_receipt_preview, null)

            /* populate & wire-up */
            val ivBig : ImageView   = view.findViewById(R.id.ivReceiptBig)
            val btnX  : ImageButton = view.findViewById(R.id.btnClose)

            ivBig.load(url)               // Coil handles caching & async loading

            val dlg = AlertDialog.Builder(
                ctx,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen
            ).setView(view).create()

            btnX.setOnClickListener { dlg.dismiss() }
            dlg.show()
        }
    }

    /* ---------- boilerplate ---------- */
    override fun onCreateViewHolder(p: ViewGroup, t: Int): VH =
        VH(ItemExpensesBinding.inflate(LayoutInflater.from(p.context), p, false))

    override fun onBindViewHolder(h: VH, pos: Int) = h.bind(items[pos])

    override fun getItemCount(): Int = items.size
}
