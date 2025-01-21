package com.kaly.bookmanagement.infrastructure.driving.web

import com.kaly.bookmanagement.domain.model.Book
import com.kaly.bookmanagement.domain.usecase.BookUseCase
import com.kaly.bookmanagement.infrastructure.driving.web.dto.BookDTO
import com.kaly.bookmanagement.infrastructure.driving.web.dto.ReserveBookDTO
import com.kaly.bookmanagement.infrastructure.driving.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ResponseStatusException

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
    @GetMapping("/{id}")
    fun getBook(@PathVariable id: Int): BookDTO {
        val book = bookUseCase.getBook(id)
        if (book == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "The book provided could not be found")
        }
        return book.toDto()
    }

    @CrossOrigin
    @PostMapping("/reserve")
    @ResponseStatus(HttpStatus.OK)
    fun ReserveBook(@RequestBody reserveBookDTO: ReserveBookDTO) {
        try {
            bookUseCase.reserveBook(reserveBookDTO.bookId, reserveBookDTO.name)
        } catch (error: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, error.message)
        } catch (error: IllegalStateException) {
            throw ResponseStatusException(HttpStatus.CONFLICT, error.message)
        }
    }

}