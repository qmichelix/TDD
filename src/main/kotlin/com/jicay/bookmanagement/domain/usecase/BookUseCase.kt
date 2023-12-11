package com.jicay.bookmanagement.domain.usecase

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import com.jicay.bookmanagement.domain.exceptions.BookAlreadyReservedException
import com.jicay.bookmanagement.domain.exceptions.BookNotFoundException

class BookUseCase(
    private val bookPort: BookPort
) {
    fun getAllBooks(): List<Book> {
        return bookPort.getAllBooks().sortedBy {
            it.name.lowercase()
        }
    }

    fun addBook(book: Book) {
        bookPort.createBook(book)
    }

     fun reserveBook(bookId: Long) {
        val book = bookPort.getBookById(bookId) ?: throw BookNotFoundException()
        if (book.isReserved) {
            throw BookAlreadyReservedException()
        }
        book.isReserved = true
        bookPort.updateBook(book)
    }
}
