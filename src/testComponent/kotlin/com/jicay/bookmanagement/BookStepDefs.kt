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
import io.restassured.path.json.JsonPath
import io.restassured.response.Response
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort
import org.junit.jupiter.api.Assertions.fail

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    companion object {
        var lastBookResult: ValidatableResponse? = null
        var lastResponse: Response? = null
    }

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        lastBookResult = null
        lastResponse = null
    }

    @When("the user creates the book {string} written by {string}")
    fun createBook(title: String, author: String) {
        lastResponse = given()
            .contentType(ContentType.JSON)
            .body("""
                {
                  "name": "$title",
                  "author": "$author"
                }
            """.trimIndent())
            .post("/books")
            .andReturn()
        println("Response for createBook: ${lastResponse?.prettyPrint()}")
        assertThat(lastResponse?.statusCode).isEqualTo(201)
    }

    @When("the user get all books")
    fun getAllBooks() {
        lastResponse = given().get("/books").andReturn()
        println("Response for getAllBooks: ${lastResponse?.prettyPrint()}")
        assertThat(lastResponse?.statusCode).isEqualTo(200)
    }

    @When("the user reserves the book with id {long}")
    fun reserveBook(bookId: Long) {
        lastBookResult = given()
            .`when`()
            .post("/books/$bookId/reserve")
            .then()
            .statusCode(200)
        println("Response for reserveBook: ${lastBookResult?.extract()?.response()?.prettyPrint()}")
    }

    @Then("the book {string} should be marked as reserved")
    fun checkBookIsReserved(title: String) {
        val response = lastBookResult?.extract()?.response()?.asString()
        println("Response for checkBookIsReserved: $response")
        if (!response.isNullOrEmpty()) {
            val jsonPath = JsonPath.from(response)
            val isReserved = jsonPath.getBoolean("find { it.name == '$title' }.isReserved")
            assertThat(isReserved).isEqualTo(true)
        } else {
            fail("La réponse est vide ou nulle")
        }
    }

    @Then("the list should contains the following books in the same order")
    fun shouldHaveListOfBooks(expectedBooks: List<Map<String, Any>>) {
        val actualBooks = lastResponse?.jsonPath()?.getList<Map<String, Any>>("")
        assertThat(actualBooks).isEqualTo(expectedBooks)
    }
}
