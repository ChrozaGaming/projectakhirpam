package com.example.projectakhirpam

/** Node /users/{uid}/items/{itemId} */
data class Item(
    var id   : String = "",
    var name : String = "",
    var stock: Int    = 0,
    var code : String = "",
    var price: Long   = 0L,          // rupiah
    var imageUrl  : String = ""
)
