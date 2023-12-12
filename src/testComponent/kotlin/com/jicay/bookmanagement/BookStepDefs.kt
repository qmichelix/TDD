package com.jicay.bookmanagement

import assertk.assertThat
import assertk.assertions.isEqualTo
import io.cucumber.java.Before
import io.cucumber.java.Scenario
import io.cucumber.java.en.Then
import io.cucumber.java.en.When
import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import io.restassured.response.Response
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    private var lastResponse: Response? = null

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        lastResponse = null
    }

    @When("the user creates the book {string} written by {string}")
    fun createBook(title: String, author: String) {
        lastResponse = given()
            .contentType(ContentType.JSON)
            .body(
                """
                    {
                      "name": "$title",
                      "author": "$author"
                    }
                """.trimIndent()
            )
            .post("/books")
            .andReturn()
        assertThat(lastResponse?.statusCode).isEqualTo(201)
    }

    @When("the user get all books")
    fun getAllBooks() {
        lastResponse = given().get("/books").andReturn()
        assertThat(lastResponse?.statusCode).isEqualTo(200)
    }

    @When("the user reserves the book with id {long}")
    fun reserveBook(bookId: Long) {
        lastResponse = given().post("/books/$bookId/reserve").andReturn()
        assertThat(lastResponse?.statusCode).isEqualTo(200)
    }

    @Then("the book {string} should be marked as reserved")
    fun checkBookIsReserved(title: String) {
        val books = lastResponse?.jsonPath()?.getList<Map<String, Any>>("")
        val book = books?.find { it["name"] == title }
        assertThat(book?.get("isReserved")).isEqualTo(true)
    }

    @Then("the list should contains the following books in the same order")
    fun shouldHaveListOfBooks(expectedBooks: List<Map<String, Any>>) {
        val actualBooks = lastResponse?.jsonPath()?.getList<Map<String, Any>>("")
        assertThat(actualBooks).isEqualTo(expectedBooks)
    }
}
