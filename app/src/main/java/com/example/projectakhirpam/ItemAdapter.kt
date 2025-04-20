package com.example.projectakhirpam

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.projectakhirpam.databinding.ItemInventoryBinding

class ItemAdapter(private val items: List<ItemModel>) :
    RecyclerView.Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(val binding: ItemInventoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            tvNamaBarang.text = item.namaBarang
            tvJumlahStok.text = item.jumlahStok.toString()
            tvKodeBarang.text = item.kodeBarang
            tvHarga.text = item.harga

            btnDelete.setOnClickListener {
                // TODO: Tambahkan aksi hapus
            }

            btnEdit.setOnClickListener {
                // TODO: Tambahkan aksi edit
            }
        }
    }
}
