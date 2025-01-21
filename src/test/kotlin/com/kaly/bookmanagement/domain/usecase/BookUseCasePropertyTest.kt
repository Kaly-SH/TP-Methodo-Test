package com.kaly.bookmanagement.domain.usecase

import com.kaly.bookmanagement.domain.model.Book
import com.kaly.bookmanagement.domain.port.BookPort
import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.next
import io.kotest.property.arbitrary.stringPattern
import io.kotest.property.checkAll
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class InMemoryBookPort : BookPort {
    private val books = mutableListOf<Book>()

    override fun getAllBooks(): List<Book> = books

    override fun createBook(book: Book) {
        books.add(book)
    }

    override fun getBook(bookId: Int): Book? {
        return books.getOrNull(bookId)
    }

    override fun reserveBook(bookId: Int, reservedBy: String) {
        val book = this.getBook(bookId)
        if (book == null) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "The book provided could not be found.")
        }
        books[bookId] = book.copy(bookedBy = reservedBy)
    }

    fun clear() {
        books.clear()
    }
}

class BookUseCasePropertyTest : FunSpec({

    val bookPort = InMemoryBookPort()
    val bookUseCase = BookUseCase(bookPort)

    test("should return all elements in the alphabetical order") {
        checkAll(Arb.int(1..100)) { nbItems ->
            bookPort.clear()

            val arb = Arb.stringPattern("""[a-z]{1,10}""")

            val titles = mutableListOf<String>()

            for (i in 1..nbItems) {
                val title = arb.next()
                titles.add(title)
                bookUseCase.addBook(Book(title, "Victor Hugo"))
            }

            val res = bookUseCase.getAllBooks()

            res.map { it.name } shouldContainExactly titles.sorted()
        }
    }
})