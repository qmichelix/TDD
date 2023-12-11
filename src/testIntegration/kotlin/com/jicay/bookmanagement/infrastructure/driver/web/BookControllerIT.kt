package com.jicay.bookmanagement.infrastructure.driver.web

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@ExtendWith(SpringExtension::class)
@WebMvcTest
class BookControllerIT {

    @MockkBean
    private lateinit var bookUseCase: BookUseCase

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun `rest route get books`() {
        // GIVEN
        every { bookUseCase.getAllBooks() } returns listOf(Book("A", "B"))

        // WHEN
        mockMvc.get("/books")
        //THEN
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content { json(
                    // language=json
                    """
                        [
                          {
                            "name": "A",
                            "author": "B"
                          }
                        ]
                    """.trimIndent()
                ) }
            }
    }

    @Test
    fun `rest route post book`() {
        justRun { bookUseCase.addBook(any()) }
    
        val bookJson = """
            {
              "name": "Les misérables",
              "author": "Victor Hugo"
            }
        """.trimIndent()
    
        mockMvc.post("/books") {
            content = bookJson
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }
    
        val expectedBook = Book(1L, "Les misérables", "Victor Hugo", false)
        verify(exactly = 1) { bookUseCase.addBook(expectedBook) }
    }

    @Test
    fun `rest route post book should return 400 when body is not good`() {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "title": "Les misérables",
                  "author": "Victor Hugo"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isBadRequest() }
        }

        verify(exactly = 0) { bookUseCase.addBook(any()) }
    }

    @Test
    fun `rest route post reserve book`() {
        val bookId = 1L
        justRun { bookUseCase.reserveBook(bookId) }

        mockMvc.post("/books/$bookId/reserve") {
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { bookUseCase.reserveBook(bookId) }
    }
}
