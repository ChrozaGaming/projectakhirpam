package com.example.projectakhirpam

/** Node  /users/{uid}/incomes/{id} */
data class Income(
    val id:            String = "",
    val date:          Long   = 0L,
    val category:      String = "",
    val amount:        Long   = 0L,
    val paymentMethod: String = "",
    val description:   String = "",
    val receiptUrl: String? = null   // ⬅️ tambahan
)

