package com.jicay.bookmanagement.domain.model

class Book(
    val id: Long,
    val name: String,
    val author: String,
    var isReserved: Boolean // Assurez-vous que cette propriété existe
)
