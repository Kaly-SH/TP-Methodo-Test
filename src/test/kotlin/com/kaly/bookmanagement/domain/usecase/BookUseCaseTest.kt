package com.kaly.bookmanagement.domain.usecase

import com.kaly.bookmanagement.domain.model.Book
import com.kaly.bookmanagement.domain.port.BookPort
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify

class BookUseCaseTest : FunSpec({

    val bookPort = mockk<BookPort>()
    val bookUseCase = BookUseCase(bookPort)

    test("get all books should returns all books sorted by name") {
        every { bookPort.getAllBooks() } returns listOf(
            Book("Les Misérables", "Victor Hugo"),
            Book("Hamlet", "William Shakespeare")
        )

        val res = bookUseCase.getAllBooks()

        res.shouldContainExactly(
            Book("Hamlet", "William Shakespeare",),
            Book("Les Misérables", "Victor Hugo",)
        )
    }

    test("add book") {
        justRun { bookPort.createBook(any()) }

        val book = Book("Les Misérables", "Victor Hugo")

        bookUseCase.addBook(book)

        verify(exactly = 1) { bookPort.createBook(book) }
    }

    test("get book should return the correct book") {
        val bookId = 1
        val expectedBook = Book("Les Misérables", "Victor Hugo")

        every { bookPort.getBook(bookId) } returns expectedBook

        val res = bookUseCase.getBook(bookId)

        res shouldBe expectedBook
    }

    test("get book should be null if book not found") {
        val bookId = 2

        every { bookPort.getBook(bookId) } returns null

        val res = bookUseCase.getBook(bookId)

        res shouldBe null
    }

    test("reserve a book should mark the book as reserved") {
        val bookId = 3
        val bookToReserve = Book("1984", "George Orwell")

        every { bookPort.getBook(bookId) } returns bookToReserve
        justRun { bookPort.reserveBook(bookId, "Maxime Mourgues") }

        bookUseCase.reserveBook(bookId, "Maxime Mourgues")

        verify(exactly = 1) { bookPort.reserveBook(bookId, "Maxime Mourgues") }
    }

    test("reserve a book should throw exception if book already reserved") {
        val bookId = 4
        val alreadyReservedBook = Book("The Great Gatsby", "F. Scott Fitzgerald", "Sandra Heraud")

        every { bookPort.getBook(bookId) } returns alreadyReservedBook

        shouldThrow<IllegalStateException> {
            bookUseCase.reserveBook(bookId, "Maxime Mourgues")
        }
    }

    test("reserve a book should throw exception if the book does not exists") {
        val bookId = 5
        every { bookPort.getBook(bookId) } returns null

        shouldThrow<IllegalArgumentException> {
            bookUseCase.reserveBook(bookId, "Sandra Heraud")
        }
    }

})