package com.kaly.bookmanagement.domain.port

import com.kaly.bookmanagement.domain.model.Book

interface BookPort {
    fun getAllBooks(): List<Book>
    fun createBook(book: Book)
}