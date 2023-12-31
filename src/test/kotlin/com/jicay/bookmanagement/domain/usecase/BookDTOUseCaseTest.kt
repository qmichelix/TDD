package com.jicay.bookmanagement.domain.usecase

import assertk.assertThat
import assertk.assertions.containsExactly
import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.port.BookPort
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class BookDTOUseCaseTest {

    @InjectMockKs
    private lateinit var bookUseCase: BookUseCase

    @MockK
    private lateinit var bookPort: BookPort

    @Test
    fun `get all books should returns all books sorted by name`() {
        every { bookPort.getAllBooks() } returns listOf(
            Book(1L, "Les Misérables", "Victor Hugo", false),
            Book(2L, "Hamlet", "William Shakespeare", false)
        )

        val res = bookUseCase.getAllBooks()

        assertThat(res.map { it.name to it.author }).containsExactly(
            "Hamlet" to "William Shakespeare",
            "Les Misérables" to "Victor Hugo"
        )
    }

    @Test
    fun `add book`() {
        justRun { bookPort.createBook(any()) }

        val book = Book(1L, "Les Misérables", "Victor Hugo", false)

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }
}
