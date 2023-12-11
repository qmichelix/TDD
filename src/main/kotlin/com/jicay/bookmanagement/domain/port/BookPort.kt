package com.jicay.bookmanagement.domain.port

import com.jicay.bookmanagement.domain.model.Book

interface BookPort {
    fun getAllBooks(): List<Book>
    fun createBook(book: Book)
    fun getBookById(bookId: Long): Book?
    fun updateBook(book: Book)
}
