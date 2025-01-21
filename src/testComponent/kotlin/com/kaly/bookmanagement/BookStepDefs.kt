package com.kaly.bookmanagement

import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.kotest.matchers.shouldBe
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.path.json.JsonPath
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0
    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
    }
    @When("the user creates the book {string} written by {string} booked by {string}")
    fun createBook(title: String, author: String, booked_by: String) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "name": "$title",
                      "author": "$author",
                      "booked_by": "$booked_by"
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
    }
    @When("the user creates the book {string} written by {string}")
    fun createBook(title: String, author: String) {
        given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "name": "$title",
                      "author": "$author"
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books")
            .then()
    }
    @When("the user get all books")
    fun getAllBooks() {
        lastBookResult = given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }
    @When("the user reserves the book with id {int} for {string}")
    fun reserveBook(bookId: Int, name: String) {
        lastBookResult = given()
            .contentType(ContentType.JSON)
            .and()
            .body(
                """
                    {
                      "bookId": $bookId,
                      "name": "$name"
                    }
                """.trimIndent()
            )
            .`when`()
            .post("/books/reserve")
            .then()
    }

    @Then("the reservation should be successful")
    fun reservationShouldBeSuccessful() {
        lastBookResult.statusCode(200)
    }

    @Then("the reservation should fail with status {int}")
    fun reservationShouldFailWithStatus(statusCode: Int) {
        lastBookResult.statusCode(statusCode)
    }

    @Then("the list should contains the following books in the same order")
    fun shouldHaveListOfBooks(payload: List<Map<String, Any>>) {
        val expectedResponse = payload.joinToString(separator = ",", prefix = "[", postfix = "]") { line ->
            """
                ${
                line.entries.joinToString(separator = ",", prefix = "{", postfix = "}") {
                    """"${it.key}": "${it.value}""""
                }
            }
            """.trimIndent()
        }
        lastBookResult.extract().body().jsonPath().prettify() shouldBe JsonPath(expectedResponse).prettify()
    }

    companion object {
        lateinit var lastBookResult: ValidatableResponse
    }
}