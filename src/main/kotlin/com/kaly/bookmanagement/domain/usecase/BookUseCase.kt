package com.kaly.bookmanagement.domain.usecase

import com.kaly.bookmanagement.domain.model.Book
import com.kaly.bookmanagement.domain.port.BookPort
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

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

    fun getBook(bookId: Int): Book? {
        return bookPort.getBook(bookId)
    }

    fun reserveBook(bookId: Int, reservedBy: String) {
        val book = this.getBook(bookId)
        if (book == null) {
            throw IllegalArgumentException("The book provided could not be found.")
        }
        if (book.bookedBy != null) {
            throw IllegalStateException("This book is already booked. Please choose another book instead.")
        }
        bookPort.reserveBook(bookId, reservedBy)
    }

}