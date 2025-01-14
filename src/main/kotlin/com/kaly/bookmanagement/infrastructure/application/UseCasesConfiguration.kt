package com.kaly.bookmanagement.infrastructure.application

import com.kaly.bookmanagement.domain.usecase.BookUseCase
import com.kaly.bookmanagement.infrastructure.driven.adapter.BookDAO
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class UseCasesConfiguration {
    @Bean
    fun bookUseCase(bookDAO: BookDAO): BookUseCase {
        return BookUseCase(bookDAO)
    }
}