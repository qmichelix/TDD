package com.jicay.bookmanagement.infrastructure.driver.web

import com.jicay.bookmanagement.domain.usecase.BookUseCase
import com.jicay.bookmanagement.infrastructure.driver.web.dto.BookDTO
import com.jicay.bookmanagement.infrastructure.driver.web.dto.toDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.http.HttpStatus
import com.jicay.bookmanagement.domain.exceptions.BookAlreadyReservedException
import com.jicay.bookmanagement.domain.exceptions.BookNotFoundException


@RestController
@RequestMapping("/books")
class BookController(
    private val bookUseCase: BookUseCase
) {
    @CrossOrigin
    @GetMapping
    fun getAllBooks(): List<BookDTO> {
        return bookUseCase.getAllBooks()
            .map { it.toDto() }
    }

    @CrossOrigin
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@RequestBody bookDTO: BookDTO) {
        bookUseCase.addBook(bookDTO.toDomain())
    }

    @CrossOrigin
    @PostMapping("/{id}/reserve")
    fun reserveBook(@PathVariable id: Long): ResponseEntity<Void> {
        return try {
            bookUseCase.reserveBook(id)
            ResponseEntity.ok().build()
        } catch (e: BookAlreadyReservedException) {
            ResponseEntity.status(HttpStatus.CONFLICT).build() // 409 Conflict
        } catch (e: BookNotFoundException) {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build() // 404 Not Found
        } catch (e: Exception) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build() // 500 Internal Server Error
        }
    }
}
