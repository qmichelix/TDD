package com.jicay.bookmanagement.infrastructure.driven.adapter

import assertk.assertThat
import assertk.assertions.containsExactlyInAnyOrder
import assertk.assertions.isEqualTo
import assertk.assertions.isNotNull
import assertk.assertions.isTrue
import com.jicay.bookmanagement.domain.model.Book
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import java.sql.ResultSet

@SpringBootTest
@Testcontainers
@ActiveProfiles("testIntegration")
class BookDAOIT {

    companion object {
        @Container
        val postgresqlContainer = PostgreSQLContainer<Nothing>("postgres:13-alpine").apply {
            withDatabaseName("testdb")
            withUsername("postgres")
            withPassword("password")
            start()
        }
    }

    @Autowired
    private lateinit var bookDAO: BookDAO

    @BeforeEach
    fun beforeEach() {
        performQuery("DELETE FROM book")
    }

    @Test
    fun `get all books from db`() {
        // GIVEN
        performQuery(
            """
               insert into book (title, author, is_reserved)
               values 
                   ('Hamlet', 'Shakespeare', false),
                   ('Les fleurs du mal', 'Beaudelaire', false),
                   ('Harry Potter', 'Rowling', false);
            """.trimIndent()
        )

        // WHEN
        val res = bookDAO.getAllBooks()

        // THEN
        assertThat(res).containsExactlyInAnyOrder(
            Book(1L, "Hamlet", "Shakespeare", false),
            Book(2L, "Les fleurs du mal", "Beaudelaire", false),
            Book(3L, "Harry Potter", "Rowling", false)
        )
    }

    @Test
    fun `create book in db`() {
        // GIVEN
        val newBook = Book(4L, "Les misérables", "Victor Hugo", false)

        // WHEN
        bookDAO.createBook(newBook)

        // THEN
        val res = performQuery("SELECT * from book WHERE title = 'Les misérables'")
        assertThat(res.size).isEqualTo(1)
        assertThat(res[0]["id"]).isNotNull()
        assertThat(res[0]["id"] is Int).isTrue()
        assertThat(res[0]["title"]).isEqualTo("Les misérables")
        assertThat(res[0]["author"]).isEqualTo("Victor Hugo")
    }

    protected fun performQuery(sql: String): List<Map<String, Any>> {
        val hikariConfig = HikariConfig()
        hikariConfig.setJdbcUrl(postgresqlContainer.jdbcUrl)
        hikariConfig.username = postgresqlContainer.username
        hikariConfig.password = postgresqlContainer.password
        hikariConfig.setDriverClassName(postgresqlContainer.driverClassName)
        val ds = HikariDataSource(hikariConfig)

        val statement = ds.getConnection().createStatement()
        statement.execute(sql)
        val resultSet = statement.resultSet
        return resultSet?.toList() ?: listOf()
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

    
}
