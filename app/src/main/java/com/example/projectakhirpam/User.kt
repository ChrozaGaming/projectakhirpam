package com.example.projectakhirpam

data class User(
    val uid: String = "",
    val fullName: String = "",
    val email: String = "",
    val balance: Long = 0L,
    val totalIncome: Long = 0L,
    val totalExpense: Long = 0L
)
