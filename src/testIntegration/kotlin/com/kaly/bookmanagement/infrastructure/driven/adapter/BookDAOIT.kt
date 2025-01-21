package com.kaly.bookmanagement.infrastructure.driven.adapter

import com.kaly.bookmanagement.domain.model.Book
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.kotest.assertions.assertSoftly
import io.kotest.core.spec.style.FunSpec
import io.kotest.extensions.spring.SpringExtension
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.testcontainers.containers.PostgreSQLContainer
import java.sql.ResultSet

@SpringBootTest
@ActiveProfiles("testIntegration")
class BookDAOIT(
    private val bookDAO: BookDAO
) : FunSpec() {
    init {
        extension(SpringExtension)

        beforeTest {
            performQuery(
                // language=sql
                "DELETE FROM book"
            )
        }

        test("get all books from db") {
            // GIVEN
            performQuery(
                // language=sql
                """
               insert into book (title, author, booked_by)
               values 
                   ('Hamlet', 'Shakespeare', null),
                   ('Les fleurs du mal', 'Beaudelaire', 'Sandra Heraud'),
                   ('Harry Potter', 'Rowling', null);
            """.trimIndent()
            )

            // WHEN
            val res = bookDAO.getAllBooks()

            // THEN
            res.shouldContainExactlyInAnyOrder(
                Book("Hamlet", "Shakespeare", null), Book("Les fleurs du mal", "Beaudelaire", "Sandra Heraud"), Book("Harry Potter", "Rowling",null)
            )
        }

        test("create book in db") {
            // GIVEN
            // WHEN
            bookDAO.createBook(Book("Les misérables", "Victor Hugo"))

            // THEN
            val res = performQuery(
                // language=sql
                "SELECT * from book"
            )

            res shouldHaveSize 1
            assertSoftly(res.first()) {
                this["id"].shouldNotBeNull().shouldBeInstanceOf<Int>()
                this["title"].shouldBe("Les misérables")
                this["author"].shouldBe("Victor Hugo")
                this["booked_by"].shouldBe(null)
            }
        }

        test("get an existent book from db") {
            // GIVEN
            performQuery(
                """
                INSERT INTO book (id, title, author, booked_by)
                VALUES (1, 'Comment faire des tests unitaire efficaces ?', 'Maxime Mourgues & Sandra Heraud', null)                    
                """.trimIndent()
            )
            // THEN
            val res = bookDAO.getBook(1)

            res.shouldNotBeNull()
            assertSoftly(res) {
                name shouldBe "Comment faire des tests unitaire efficaces ?"
                author shouldBe "Maxime Mourgues & Sandra Heraud"
                booked_by shouldBe null
            }
        }

        test("get a non-existent book from db") {
            // GIVEN
            // THEN
            val res = bookDAO.getBook(999) // peut être techniquement n'importe quel id puisque la db est réinitialisée

            res.shouldBeNull()
        }

        test("reserve a book from db") {
            // GIVEN
            performQuery(
                """
                INSERT INTO book (id, title, author, booked_by)
                VALUES (1, 'Le Château de Hurle', 'Diana Wynne Jones', null)                    
                """.trimIndent()
            )
            // THEN
            bookDAO.reserveBook(1,"Sandra Heraud")

            val res = bookDAO.getBook(1)

            res.shouldNotBeNull()
            assertSoftly(res) {
                name shouldBe "Le Château de Hurle"
                author shouldBe "Diana Wynne Jones"
                booked_by shouldBe "Sandra Heraud"
            }
        }

        afterSpec {
//            container.stop()
        }
    }

    companion object {
        private val container = PostgreSQLContainer<Nothing>("postgres:13-alpine")

        init {
            container.start()
            System.setProperty("spring.datasource.url", container.jdbcUrl)
            System.setProperty("spring.datasource.username", container.username)
            System.setProperty("spring.datasource.password", container.password)
        }

        private fun ResultSet.toList(): List<Map<String, Any>> {
            val md = this.metaData
            val columns = md.columnCount
            val rows: MutableList<Map<String, Any>> = ArrayList()
            while (this.next()) {
                val row: MutableMap<String, Any> = HashMap(columns)
                for (i in 1..columns) {
                    row[md.getColumnName(i)] = this.getObject(i)
                }
                rows.add(row)
            }
            return rows
        }

        fun performQuery(sql: String): List<Map<String, Any>> {
            val hikariConfig = HikariConfig()
            hikariConfig.setJdbcUrl(container.jdbcUrl)
            hikariConfig.username = container.username
            hikariConfig.password = container.password
            hikariConfig.setDriverClassName(container.driverClassName)

            val ds = HikariDataSource(hikariConfig)

            val statement = ds.connection.createStatement()
            statement.execute(sql)
            val resultSet = statement.resultSet
            return resultSet?.toList() ?: listOf()
        }
    }
}