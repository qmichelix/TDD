package com.jicay.bookmanagement.infrastructure.driver.web

import com.jicay.bookmanagement.domain.model.Book
import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.infrastructure.driver.web.dto.BookDTO
import com.jicay.bookmanagement.infrastructure.driver.web.dto.toDto
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import com.ninjasquad.springmockk.MockkBean
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(BookController::class)
class BookControllerIT {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var bookUseCase: BookUseCase

    @Test
    fun `rest route get books`() {
        // GIVEN
        every { bookUseCase.getAllBooks() } returns listOf(
            Book(1L, "A", "Author A", false),
            Book(2L, "B", "Author B", false)
        )

        // WHEN & THEN
        mockMvc.get("/books")
            .andExpect {
                status().isOk()
                content().contentType(APPLICATION_JSON)
                content().json(
                    // language=json
                    """
                        [
                          {
                            "id": 1,
                            "name": "A",
                            "author": "Author A",
                            "isReserved": false
                          },
                          {
                            "id": 2,
                            "name": "B",
                            "author": "Author B",
                            "isReserved": false
                          }
                        ]
                    """.trimIndent()
                )
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

        verify(exactly = 1) { bookUseCase.addBook(match { it.name == "Les misérables" && it.author == "Victor Hugo" }) }
    }


    @Test
    fun `rest route post book should return 400 when body is not good`() {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
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

        mockMvc.post("/books/$bookId/reserve")
            .andExpect {
                status().isOk
            }

        verify(exactly = 1) { bookUseCase.reserveBook(bookId) }
    }
}
