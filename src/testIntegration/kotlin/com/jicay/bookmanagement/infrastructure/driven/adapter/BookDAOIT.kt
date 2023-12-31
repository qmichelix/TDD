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
        assertThat(res.map { it.name }).containsExactlyInAnyOrder("Hamlet", "Les fleurs du mal", "Harry Potter")
        assertThat(res.map { it.author }).containsExactlyInAnyOrder("Shakespeare", "Beaudelaire", "Rowling")
        assertThat(res.all { !it.isReserved }).isTrue()
    }

    @Test
    fun `create book in db`() {
        // GIVEN
        val newBook = Book(id = 0L, name = "Les misérables", author = "Victor Hugo", isReserved = false) // ID fixé à 0 ou un autre nombre arbitraire
    
        // WHEN
        bookDAO.createBook(newBook)
    
        // THEN
        val res = performQuery("SELECT * from book WHERE title = 'Les misérables'")
        assertThat(res.size).isEqualTo(1)
        assertThat(res[0]["title"]).isEqualTo("Les misérables")
        assertThat(res[0]["author"]).isEqualTo("Victor Hugo")
        assertThat(res[0]["is_reserved"]).isEqualTo(false)
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

    companion object {
        @Container
        private val postgresqlContainer: PostgreSQLContainer<*> = PostgreSQLContainer("postgres:13-alpine")

        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            postgresqlContainer.start()
        }

        @JvmStatic
        @AfterAll
        fun afterAll() {
            postgresqlContainer.stop()
        }

        @JvmStatic
        @DynamicPropertySource
        fun configureProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl)
            registry.add("spring.datasource.username", postgresqlContainer::getUsername)
            registry.add("spring.datasource.password", postgresqlContainer::getPassword)
        }
    }
}
