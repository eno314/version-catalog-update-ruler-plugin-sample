package jp.eno314.vcu.pdate.ruler.sample.integration

import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteRequest
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteResponse
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext

@SpringBootTest(
    classes = [jp.eno314.vcu.pdate.ruler.sample.VersionCatalogUpdateRulerSampleApplication::class, RssIntegrationTest.TestConfig::class],
)
class RssIntegrationTest {
    @TestConfiguration
    class TestConfig {
        @Bean
        @Primary
        fun mockRssClient(): RssClient {
            return object : RssClient(
                org.springframework.web.client.RestClient
                    .create(),
            ) {
                override fun fetch(request: RssFetchRemoteRequest): RssFetchRemoteResponse {
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
                    return RssFetchRemoteResponse(rawXml = dummyXml)
                }
            }
        }
    }

    @Autowired
    private lateinit var wac: WebApplicationContext

    private val mockMvc: MockMvc by lazy {
        MockMvcBuilders.webAppContextSetup(wac).build()
    }

    @Test
    fun `getRss returns 200 OK with valid rss_url`() {
        mockMvc
            .get("/api/rss") {
                param("rssUrl", "https://example.com/rss")
            }.andExpect {
                status { isOk() }
                jsonPath("$.site_info.title").value("サイトのタイトル")
                jsonPath("$.items[0].title").value("記事のタイトル")
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
