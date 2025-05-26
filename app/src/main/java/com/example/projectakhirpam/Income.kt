package com.example.projectakhirpam

/** Node  /users/{uid}/incomes/{id} */
data class Income(
    var id            : String = "",
    var date          : Long   = 0L,          // disimpan millis
    var category      : String = "",
    var amount        : Long   = 0L,          // nominal murni
    var paymentMethod : String = "",
    var description   : String = ""
)
