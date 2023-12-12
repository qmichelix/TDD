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
import io.restassured.response.ValidatableResponse
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    companion object {
        var lastBookResult: ValidatableResponse? = null
    }

    @Before
    fun setup(scenario: Scenario) {
        RestAssured.baseURI = "http://localhost:$port"
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails()
        lastBookResult = null
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
            .statusCode(201)
    }

    @When("the user get all books")
    fun getAllBooks() {
        lastBookResult = given()
            .`when`()
            .get("/books")
            .then()
            .statusCode(200)
    }

    @When("the user reserves the book with id {long}")
    fun reserveBook(bookId: Long) {
        lastBookResult = given()
            .`when`()
            .post("/books/$bookId/reserve")
            .then()
            .statusCode(200)
    }

    @Then("the book {string} should be marked as reserved")
    fun checkBookIsReserved(title: String) {
        val book = lastBookResult?.extract()?.jsonPath()?.getJsonObject("find { it.name == '$title' }")
        assertThat(book?.get("isReserved")).isEqualTo(true)
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
        assertThat(lastBookResult?.extract()?.body()?.jsonPath()?.prettify())
            .isEqualTo(JsonPath(expectedResponse).prettify())
    }
}
