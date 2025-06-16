package com.example.projectakhirpam

/**
 * Node  /users/{uid}/expenses/{expenseId}
 */
data class Expense(
    var id           : String = "",
    var date         : Long   = 0L,
    var category     : String = "",
    var amount       : Long   = 0L,
    var paymentMethod: String = "",
    var description  : String = "",
    var recipient    : String = "",
    var receiptUrl   : String = ""   // URI lokal - tidak memakai Firebase Storage
)