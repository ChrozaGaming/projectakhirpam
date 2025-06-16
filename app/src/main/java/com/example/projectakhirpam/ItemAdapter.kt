package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.projectakhirpam.databinding.DialogReceiptPreviewBinding
import com.example.projectakhirpam.databinding.ItemInventoryBinding
import java.text.NumberFormat
import java.util.*

class ItemAdapter(
    private val onDelete: (Item) -> Unit,
    private val onEdit  : (Item) -> Unit
) : RecyclerView.Adapter<ItemAdapter.VH>() {

    private val list = mutableListOf<Item>()
    private val nf   = NumberFormat.getCurrencyInstance(Locale("in","ID"))

    fun submit(data: List<Item>){
        list.clear(); list.addAll(data); notifyDataSetChanged()
    }

    inner class VH(val b: ItemInventoryBinding): RecyclerView.ViewHolder(b.root){
        init{
            b.btnDelete.setOnClickListener {
                val p = adapterPosition
                if (p!=RecyclerView.NO_POSITION) onDelete(list[p])
            }
            b.btnEdit.setOnClickListener {
                val p = adapterPosition
                if (p!=RecyclerView.NO_POSITION) onEdit(list[p])
            }
            b.ivEye.setOnClickListener {
                val p = adapterPosition
                if (p!=RecyclerView.NO_POSITION){
                    list[p].imageUrl.takeIf { it.isNotBlank() }?.let { show(it) }
                }
            }
        }
        fun bind(i: Item)=with(b){
            tvNamaBarang.text = i.name
            tvJumlahStok.text = i.stock.toString()
            tvKodeBarang.text = i.code
            tvHarga.text      = nf.format(i.price)
            ivEye.visibility  =
                if (i.imageUrl.isBlank()) View.GONE else View.VISIBLE
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

    override fun onCreateViewHolder(p: ViewGroup, t:Int)=
        VH(ItemInventoryBinding.inflate(LayoutInflater.from(p.context),p,false))
    override fun onBindViewHolder(h: VH, p:Int)=h.bind(list[p])
    override fun getItemCount()=list.size
}
