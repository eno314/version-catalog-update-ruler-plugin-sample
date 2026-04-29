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

    @Test
    fun `getRss returns 200 OK with valid atom_url`() {
        val dummyAtomXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <feed xmlns="http://www.w3.org/2005/Atom">
                <title>サイトのタイトル</title>
                <link rel="alternate" href="https://example.com"/>
                <subtitle>サイトの概要説明</subtitle>
                <entry>
                    <id>https://example.com/article/123</id>
                    <title>記事のタイトル</title>
                    <link href="https://example.com/article/123"/>
                    <summary><![CDATA[<p>ヘッドラインのテキストやHTML...</p>]]></summary>
                    <published>2026-04-25T10:00:00Z</published>
                    <author>
                        <name>著者名</name>
                    </author>
                    <category term="テクノロジー"/>
                    <category term="プログラミング"/>
                </entry>
            </feed>
            """.trimIndent()

        mockServer
            .expect(requestTo("https://example.com/atom"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(dummyAtomXml, MediaType.APPLICATION_XML))

        mockMvc
            .get("/api/rss") {
                param("rssUrl", "https://example.com/atom")
            }.andExpect {
                status { isOk() }
                jsonPath("$.site_info.title").value("サイトのタイトル")
                jsonPath("$.items[0].title").value("記事のタイトル")
            }

        mockServer.verify()
    }

    @Test
    fun `getRss returns 200 OK with valid namespaced rss_url`() {
        val namespacedXml =
            """
            <?xml version="1.0" encoding="UTF-8"?>
            <rss xmlns:webfeeds="http://webfeeds.org/rss/1.0" xmlns:note="https://note.com" xmlns:atom="http://www.w3.org/2005/Atom" xmlns:media="http://search.yahoo.com/mrss/" version="2.0">
                <channel>
                    <title>Namespaced Channel</title>
                    <link>https://namespaced.example.com</link>
                    <description>Namespaced Channel Description</description>
                    <item>
                        <guid>https://namespaced.example.com/article/123</guid>
                        <title>Namespaced Article</title>
                        <link>https://namespaced.example.com/article/123</link>
                        <description><![CDATA[<p>Namespaced content...</p>]]></description>
                        <pubDate>2026-04-25T10:00:00Z</pubDate>
                        <author>Namespaced Author</author>
                        <category>Namespaced</category>
                    </item>
                </channel>
            </rss>
            """.trimIndent()

        mockServer
            .expect(requestTo("https://namespaced.example.com/rss"))
            .andExpect(method(HttpMethod.GET))
            .andRespond(withSuccess(namespacedXml, MediaType.APPLICATION_XML))

        mockMvc
            .get("/api/rss") {
                param("rssUrl", "https://namespaced.example.com/rss")
            }.andExpect {
                status { isOk() }
                jsonPath("$.site_info.title").value("Namespaced Channel")
                jsonPath("$.items[0].title").value("Namespaced Article")
            }

        mockServer.verify()
    }
}
