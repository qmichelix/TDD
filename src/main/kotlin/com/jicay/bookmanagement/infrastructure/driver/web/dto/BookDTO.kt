package com.jicay.bookmanagement.infrastructure.driver.web.dto

import com.jicay.bookmanagement.domain.model.Book

data class BookDTO(
    val id: Long, // Ajoutez l'ID du livre
    val name: String,
    val author: String,
    val isReserved: Boolean // Ajoutez l'état de réservation du livre
) {
    fun toDomain(): Book {
        return Book(
            id = this.id,
            name = this.name,
            author = this.author,
            isReserved = this.isReserved
        )
    }
}

fun Book.toDto() = BookDTO(
    id = this.id,
    name = this.name,
    author = this.author,
    isReserved = this.isReserved
)
