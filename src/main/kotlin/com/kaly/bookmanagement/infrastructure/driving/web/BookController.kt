package com.kaly.bookmanagement.infrastructure.driving.web

import com.kaly.bookmanagement.domain.usecase.BookUseCase
import com.kaly.bookmanagement.infrastructure.driving.web.dto.BookDTO
import com.kaly.bookmanagement.infrastructure.driving.web.dto.toDto
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

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

}