package com.jicay.bookmanagement.domain.model

data class Book(
    val id: Long,
    val name: String,
    val author: String,
    var isReserved: Boolean
)
