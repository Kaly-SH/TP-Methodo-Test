package com.kaly.bookmanagement.infrastructure.driving.web

import com.kaly.bookmanagement.domain.model.Book
import com.kaly.bookmanagement.domain.usecase.BookUseCase
import com.ninjasquad.springmockk.MockkBean
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.mockk.every
import io.mockk.justRun
import io.mockk.verify
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post

@WebMvcTest
class BookControllerIT(
    @MockkBean private val bookUseCase: BookUseCase,
    private val mockMvc: MockMvc
) : FunSpec({
    extension(SpringExtension)

    test("rest route get books") {
        // GIVEN
        every { bookUseCase.getAllBooks() } returns listOf(Book("A", "B"))

        // WHEN
        mockMvc.get("/books")
            //THEN
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content {
                    json(
                        // language=json
                        """
                        [
                          {
                            "name": "A",
                            "author": "B",
                            "booked_by": null
                          }
                        ]
                        """.trimIndent()
                    )
                }
            }
    }

    test("rest route post book") {
        justRun { bookUseCase.addBook(any()) }

        mockMvc.post("/books") {
            // language=json
            content = """
                {
                  "name": "Les misérables",
                  "author": "Victor Hugo"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isCreated() }
        }

        val expected = Book(
            name = "Les misérables",
            author = "Victor Hugo"
        )

        verify(exactly = 1) { bookUseCase.addBook(expected) }
    }

    test("rest route post book should return 400 when body is not good") {
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

    test("rest route get book by id") {
        val bookId = 3
        every { bookUseCase.getBook(bookId) } returns Book("Harry Potter à l'école des sorciers", "J.K Rowling")

        mockMvc.get("/books/3")
            .andExpect {
                status { isOk() }
                content { content { APPLICATION_JSON } }
                content {
                    json(
                        // language=json
                        """
                        
                          {
                            "name": "Harry Potter à l'école des sorciers",
                            "author": "J.K Rowling",
                            "booked_by": null
                          }
                        
                        """.trimIndent()
                    )
                }
            }
    }

    test("rest route get non-existent book") {
        val bookId = 9
        every { bookUseCase.getBook(bookId) } returns null

        mockMvc.get("/books/9")
            .andExpect{
                status { isNotFound() }
            }
    }

    test("rest route reserve a book") {
        val bookId = 4
        every { bookUseCase.getBook(bookId) } returns Book("Gardiens des cités perdues", "Shannon Messenger")
        justRun { bookUseCase.reserveBook(bookId, "Sandra Heraud") }

        mockMvc.post("/books/reserve") {
            // language=json
            content = """
                {
                  "bookId": 4,
                  "name": "Sandra Heraud"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isOk() }
        }

        verify(exactly = 1) { bookUseCase.reserveBook(bookId, "Sandra Heraud") }
    }

    test("rest route reserve a book already reserved") {
        val bookId = 5
        every { bookUseCase.getBook(bookId) } returns Book("Gardiens des cités perdues", "Shannon Messenger", "Sandra Heraud")
        justRun { bookUseCase.reserveBook(bookId, "Sandra Heraud") }

        mockMvc.post("/books/reserve") {
            // language=json
            content = """
                {
                  "bookId": 5,
                  "name": "Sandra Heraud"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isConflict() }
        }

//        shouldThrow<IllegalStateException> { bookUseCase.reserveBook(bookId, "Sandra Heraud") }
    }

    test("rest route reserve a non-existent book") {
        val bookId = 1
        every { bookUseCase.getBook(bookId) } returns null
        justRun { bookUseCase.reserveBook(bookId, "Maxime Mourgues") }

        mockMvc.post("/books/reserve") {
            // language=json
            content = """
                {
                  "bookId": 1 ,
                  "name": "Maxime Mourgues"
                }
            """.trimIndent()
            contentType = APPLICATION_JSON
            accept = APPLICATION_JSON
        }.andExpect {
            status { isNotFound() }
        }

//        shouldThrow<IllegalArgumentException> { bookUseCase.reserveBook(bookId, "Maxime Mourgues") }
    }
})