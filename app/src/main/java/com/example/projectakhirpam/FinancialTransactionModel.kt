package com.example.projectakhirpam

data class FinancialTransactionModel(
    val id: String = "",
    val date: String,
    val category: String,
    val amount: String,
    val paymentMethod: String
)
