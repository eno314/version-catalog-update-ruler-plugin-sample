package jp.eno314.vcu.pdate.ruler.sample.integration

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
class RssIntegrationTest {
    @Autowired
    private lateinit var wac: WebApplicationContext

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @Test
    fun `getRss returns 200 OK with valid rss_url`() {
        mockMvc
            .get("/api/rss") {
                param("rss_url", "https://example.com/rss")
            }.andExpect {
                status { isOk() }
                jsonPath("$.site_info.title") { value("サイトのタイトル") }
                jsonPath("$.items[0].title") { value("記事のタイトル") }
            }
    }

    @Test
    fun `getRss returns 400 Bad Request when rss_url is missing`() {
        mockMvc
            .get("/api/rss")
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `getRss returns 400 Bad Request when rss_url is invalid format`() {
        mockMvc
            .get("/api/rss") {
                param("rss_url", "not-a-url")
            }.andExpect {
                status { isBadRequest() }
            }
    }
}
