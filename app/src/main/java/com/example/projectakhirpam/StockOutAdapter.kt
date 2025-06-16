package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.projectakhirpam.databinding.DialogReceiptPreviewBinding
import com.example.projectakhirpam.databinding.ItemStockoutBinding
import java.text.NumberFormat
import java.util.*

class StockOutAdapter(private val list: List<StockOut>)
    : RecyclerView.Adapter<StockOutAdapter.VH>() {

    private val nf = NumberFormat.getCurrencyInstance(Locale("in","ID"))

    inner class VH(val b: ItemStockoutBinding) : RecyclerView.ViewHolder(b.root){
        init {
            b.btnViewReceipt.setOnClickListener {
                val p = bindingAdapterPosition
                if (p!=RecyclerView.NO_POSITION){
                    list[p].receiptUrl.takeIf { it.isNotBlank() }?.let { show(it) }
                }
            }
        }
        fun bind(d: StockOut)=with(b){
            tvItemName.text   = d.itemName
            tvQty.text        = d.qty.toString()
            tvUnitPrice.text  = nf.format(d.unitPrice)
            tvTotalPrice.text = nf.format(d.totalPrice)
            tvDestination.text= d.destination
            tvDate.text       = d.date
            btnViewReceipt.visibility =
                if (d.receiptUrl.isBlank()) View.GONE else View.VISIBLE
        }
        private fun show(url:String){
            val ctx = b.root.context
            val v   = DialogReceiptPreviewBinding.inflate(LayoutInflater.from(ctx))
            val dlg = AlertDialog.Builder(ctx,
                android.R.style.Theme_Black_NoTitleBar_Fullscreen)
                .setView(v.root).create()
            v.ivReceiptBig.load(url){
                placeholder(R.drawable.ic_image_placeholder)
                error(R.drawable.ic_broken_image)
            }
            v.btnClose.setOnClickListener { dlg.dismiss() }
            dlg.show()
        }
    }

    override fun onCreateViewHolder(p: ViewGroup,t:Int)=
        VH(ItemStockoutBinding.inflate(LayoutInflater.from(p.context),p,false))
    override fun onBindViewHolder(h: VH,p:Int)=h.bind(list[p])
    override fun getItemCount()=list.size
}
