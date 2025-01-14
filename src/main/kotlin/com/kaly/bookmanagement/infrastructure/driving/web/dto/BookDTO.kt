package com.kaly.bookmanagement.infrastructure.driving.web.dto

import com.kaly.bookmanagement.domain.model.Book

data class BookDTO(val name: String, val author: String) {
    fun toDomain(): Book {
        return Book(
            name = this.name,
            author = this.author
        )
    }
}

fun Book.toDto() = BookDTO(
    name = this.name,
    author = this.author
)