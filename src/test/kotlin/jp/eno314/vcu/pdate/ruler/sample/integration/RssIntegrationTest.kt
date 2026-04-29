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
import org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo
import org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.web.client.RestClient
import org.springframework.web.context.WebApplicationContext

@SpringBootTest
@AutoConfigureMockMvc
class RssIntegrationTest {
    @Autowired
    private lateinit var wac: WebApplicationContext

    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var restClientBuilder: RestClient.Builder

    private lateinit var mockServer: MockRestServiceServer

    @BeforeEach
    fun setup() {
        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build()
    }

    @Test
    fun `getRss returns 200 OK with valid rss_url`() {
        val dummyXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss version="2.0">
                <channel>
                    <title>サイトのタイトル</title>
                    <link>https://example.com</link>
                    <description>サイトの概要説明</description>
                    <item>
                        <guid>https://example.com/article/123</guid>
                        <title>記事のタイトル</title>
                        <link>https://example.com/article/123</link>
                        <description><![CDATA[<p>ヘッドラインのテキストやHTML...</p>]]></description>
                        <pubDate>2026-04-25T10:00:00Z</pubDate>
                        <author>著者名</author>
                        <category>テクノロジー</category>
                        <category>プログラミング</category>
                    </item>
                </channel>
            </rss>
            """.trimIndent()

        mockServer
            .expect(requestTo("https://example.com/rss"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(dummyXml, MediaType.APPLICATION_XML))

        mockMvc
            .get("/api/rss") {
                param("rssUrl", "https://example.com/rss")
            }.andExpect {
                status { isOk() }
                jsonPath("$.site_info.title").value("サイトのタイトル")
                jsonPath("$.items[0].title").value("記事のタイトル")
            }

        mockServer.verify()
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
