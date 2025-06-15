package com.example.projectakhirpam

/** Model data untuk riwayat Stock Out */

data class StockOut(
    val id: String = "",
    val itemId: String = "",
    val itemName: String = "",
    val qty: Int = 0,
    val unitPrice: Long = 0,
    val totalPrice: Long = 0,
    val date: String = "",
    val destination: String = "",
    val note: String = "",
    val timestamp: Long = 0
)


