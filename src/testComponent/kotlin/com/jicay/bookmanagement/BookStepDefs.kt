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
import org.springframework.boot.test.web.server.LocalServerPort

class BookStepDefs {
    @LocalServerPort
    private var port: Int? = 0

    companion object {
        var lastResponse: Response? = null
    }

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
            .body("""{"name": "$title", "author": "$author"}""")
            .post("/books")
            .andReturn()
        println("Create Book Response: ${lastResponse?.asString()}")
        assertThat(lastResponse?.statusCode).isEqualTo(201)
    }

    @When("the user get all books")
    fun getAllBooks() {
        lastResponse = given().get("/books").andReturn()
        println("Get All Books Response: ${lastResponse?.asString()}")
        assertThat(lastResponse?.statusCode).isEqualTo(200)
    }

    @When("the user reserves the book with id {long}")
    fun reserveBook(bookId: Long) {
        lastResponse = given().post("/books/$bookId/reserve").andReturn()
        println("Reserve Book Response: ${lastResponse?.asString()}")
        assertThat(lastResponse?.statusCode).isEqualTo(200)
    }

    @Then("the book {string} should be marked as reserved")
    fun checkBookIsReserved(title: String) {
        val response = lastResponse?.asString()
        if (response.isNullOrEmpty()) {
            println("Response is empty or null")
        } else {
            val jsonPath = JsonPath.from(response)
            val isReserved = jsonPath.getBoolean("find { it.name == '$title' }.isReserved")
            assertThat(isReserved).isEqualTo(true)
        }
    }

    @Then("the list should contains the following books in the same order")
    fun shouldHaveListOfBooks(expectedBooks: List<Map<String, Any>>) {
        val actualBooks = lastResponse?.jsonPath()?.getList<Map<String, Any>>("")
        println("Expected Books: $expectedBooks")
        println("Actual Books: $actualBooks")
        assertThat(actualBooks).isEqualTo(expectedBooks)
    }
}
