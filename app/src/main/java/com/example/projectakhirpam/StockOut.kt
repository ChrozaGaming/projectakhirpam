package com.example.projectakhirpam

/** Node  /users/{uid}/stockouts/{id} */
data class StockOut(
    val id          : String = "",
    val itemId      : String = "",
    val itemName    : String = "",
    val qty         : Int    = 0,
    val unitPrice   : Long   = 0,
    val totalPrice  : Long   = 0,
    val date        : String = "",
    val destination : String = "",
    val note        : String = "",
    val timestamp   : Long   = 0,
    val receiptUrl  : String = ""          //  ← NEW
)
