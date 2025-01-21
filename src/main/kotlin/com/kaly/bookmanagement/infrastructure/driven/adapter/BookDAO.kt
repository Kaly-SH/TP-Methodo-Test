package com.kaly.bookmanagement.infrastructure.driven.adapter

import com.kaly.bookmanagement.domain.model.Book
import com.kaly.bookmanagement.domain.port.BookPort
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Service

@Service
class BookDAO(private val namedParameterJdbcTemplate: NamedParameterJdbcTemplate): BookPort {
    override fun getAllBooks(): List<Book> {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK", MapSqlParameterSource()) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    booked_by = rs.getString("booked_by")
                )
            }
    }

    override fun createBook(book: Book) {
        namedParameterJdbcTemplate
            .update("INSERT INTO BOOK (title, author, booked_by) values (:title, :author, :booked_by)", mapOf(
                "title" to book.name,
                "author" to book.author,
                "booked_by" to book.booked_by,
            ))
    }

    override fun getBook(bookId: Int): Book? {
        return namedParameterJdbcTemplate
            .query("SELECT * FROM BOOK WHERE id = :id", MapSqlParameterSource("id", bookId)) { rs, _ ->
                Book(
                    name = rs.getString("title"),
                    author = rs.getString("author"),
                    booked_by = rs.getString("booked_by")
                )
        }.firstOrNull()
    }

    override fun reserveBook(bookId: Int, reservedBy: String) {
        namedParameterJdbcTemplate
            .update("UPDATE BOOK SET booked_by = :name WHERE id = :id", mapOf(
                "name" to reservedBy,
                "id" to bookId,
            ))
    }
}