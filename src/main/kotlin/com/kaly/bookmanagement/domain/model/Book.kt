package com.kaly.bookmanagement.domain.model

data class Book(val name: String, val author: String, val booked_by: String? = null)
