package com.kaly.bookmanagement.infrastructure.driving.web.dto

import com.kaly.bookmanagement.domain.model.Book

data class BookDTO(val name: String, val author: String, val bookedBy: String?) {
    fun toDomain(): Book {
        return Book(
            name = this.name,
            author = this.author,
            bookedBy = this.bookedBy,
        )
    }
}

fun Book.toDto() = BookDTO(
    name = this.name,
    author = this.author,
    bookedBy = this.bookedBy
)