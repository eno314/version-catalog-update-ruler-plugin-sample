package jp.eno314.vcu.pdate.ruler.sample.integration

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.HttpMethod
import org.springframework.http.MediaType
import org.springframework.test.web.client.MockRestServiceServer
import org.springframework.test.web.client.match.MockRestRequestMatchers.method
import org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.web.client.RestClient

private const val GOOGLE_BOOKS_BASE_URL = "https://www.googleapis.com/books/v1/volumes"

@SpringBootTest
@AutoConfigureMockMvc
class BooksIntegrationTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var restClientBuilder: RestClient.Builder

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setup() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build()
    }

    private fun buildDummyResponse(
        totalItems: Int,
        items: String,
    ): String =
        """
        {
          "kind": "books#volumes",
          "totalItems": $totalItems,
          "items": [$items]
        }
        """.trimIndent()

    private val dummyVolumeJson =
        """
        {
          "id": "abc123",
          "volumeInfo": {
            "title": "Clean Code",
            "authors": ["Robert C. Martin"],
            "publisher": "Prentice Hall",
            "publishedDate": "2008-08-01",
            "description": "A handbook of agile software craftsmanship.",
            "imageLinks": {
              "smallThumbnail": "https://example.com/small.jpg",
              "thumbnail": "https://example.com/thumb.jpg"
            },
            "infoLink": "https://books.google.com/books?id=abc123"
          }
        }
        """.trimIndent()

    @Test
    fun `searchBooks returns 200 OK with valid title and googleBooksApiKey`() {
        mockServer
            .expect(
                requestToUriTemplate(
                    "$GOOGLE_BOOKS_BASE_URL?q={q}&key={key}&printType={printType}&langRestrict={langRestrict}",
                    "intitle:Clean Code",
                    "test-key",
                    "all",
                    "ja",
                ),
            ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(buildDummyResponse(1, dummyVolumeJson), MediaType.APPLICATION_JSON))

        mockMvc
            .get("/api/books") {
                param("title", "Clean Code")
                param("googleBooksApiKey", "test-key")
            }.andExpect {
                status { isOk() }
                jsonPath("$.total_items").value(1)
                jsonPath("$.books[0].title").value("Clean Code")
                jsonPath("$.books[0].authors[0]").value("Robert C. Martin")
                jsonPath("$.books[0].publisher").value("Prentice Hall")
                jsonPath("$.books[0].id").value("abc123")
            }

        mockServer.verify()
    }

    @Test
    fun `searchBooks sends googleBooksApiKey as query parameter`() {
        mockServer
            .expect(
                requestToUriTemplate(
                    "$GOOGLE_BOOKS_BASE_URL?q={q}&key={key}&printType={printType}&langRestrict={langRestrict}",
                    "intitle:Kotlin",
                    "test-api-key",
                    "all",
                    "ja",
                ),
            ).andExpect(method(HttpMethod.GET))
            .andExpect(queryParam("key", "test-api-key"))
            .andRespond(withSuccess(buildDummyResponse(0, ""), MediaType.APPLICATION_JSON))

        mockMvc
            .get("/api/books") {
                param("title", "Kotlin")
                param("googleBooksApiKey", "test-api-key")
            }.andExpect {
                status { isOk() }
                jsonPath("$.total_items").value(0)
            }

        mockServer.verify()
    }

    @Test
    fun `searchBooks returns 200 OK with multibyte title (Japanese)`() {
        val japaneseTitle = "吾輩は猫である"
        val query = "intitle:$japaneseTitle"

        mockServer
            .expect(
                requestToUriTemplate(
                    "$GOOGLE_BOOKS_BASE_URL?q={q}&key={key}&printType={printType}&langRestrict={langRestrict}",
                    query,
                    "test-key",
                    "all",
                    "ja",
                ),
            ).andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    buildDummyResponse(
                        1,
                        dummyVolumeJson.replace("Clean Code", japaneseTitle),
                    ),
                    MediaType.APPLICATION_JSON,
                ),
            )

        mockMvc
            .get("/api/books") {
                param("title", japaneseTitle)
                param("googleBooksApiKey", "test-key")
            }.andExpect {
                status { isOk() }
                jsonPath("$.books[0].title").value(japaneseTitle)
            }

        mockServer.verify()
    }

    @Test
    fun `searchBooks returns 400 Bad Request when title is missing`() {
        mockMvc
            .get("/api/books")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `searchBooks returns 400 Bad Request when googleBooksApiKey is missing`() {
        mockMvc
            .get("/api/books") {
                param("title", "Clean Code")
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `searchBooks returns 400 Bad Request when title is blank`() {
        mockMvc
            .get("/api/books") {
                param("title", "")
                param("googleBooksApiKey", "test-key")
            }.andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `searchBooks returns 200 OK with empty books when totalItems is 0`() {
        mockServer
            .expect(
                requestToUriTemplate(
                    "$GOOGLE_BOOKS_BASE_URL?q={q}&key={key}&printType={printType}&langRestrict={langRestrict}",
                    "intitle:UnknownXYZ",
                    "test-key",
                    "all",
                    "ja",
                ),
            ).andExpect(method(HttpMethod.GET))
            .andRespond(
                withSuccess(
                    """{"kind":"books#volumes","totalItems":0}""",
                    MediaType.APPLICATION_JSON,
                ),
            )

        mockMvc
            .get("/api/books") {
                param("title", "UnknownXYZ")
                param("googleBooksApiKey", "test-key")
            }.andExpect {
                status { isOk() }
                jsonPath("$.total_items").value(0)
                jsonPath("$.books").isArray
                jsonPath("$.books").isEmpty
            }

        mockServer.verify()
    }

    @Test
    fun `searchBooks returns 200 OK with all parameters specified`() {
        val query = "intitle:Clean Code+inauthor:Robert C. Martin+inpublisher:Prentice Hall+subject:Programming"
        mockServer
            .expect(
                requestToUriTemplate(
                    "$GOOGLE_BOOKS_BASE_URL?q={q}&key={key}&printType={printType}&langRestrict={langRestrict}",
                    query,
                    "test-key",
                    "books",
                    "en",
                ),
            ).andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(buildDummyResponse(1, dummyVolumeJson), MediaType.APPLICATION_JSON))

        mockMvc
            .get("/api/books") {
                param("title", "Clean Code")
                param("googleBooksApiKey", "test-key")
                param("author", "Robert C. Martin")
                param("publisher", "Prentice Hall")
                param("subject", "Programming")
                param("printType", "books")
                param("langRestrict", "en")
            }.andExpect {
                status { isOk() }
                jsonPath("$.total_items").value(1)
            }

        mockServer.verify()
    }
}
