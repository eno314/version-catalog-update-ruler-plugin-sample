package jp.eno314.vcu.pdate.ruler.sample.controller

import io.mockk.every
import io.mockk.mockk
import jp.eno314.vcu.pdate.ruler.sample.service.RssFetchRequest
import jp.eno314.vcu.pdate.ruler.sample.service.RssFetchResponse
import jp.eno314.vcu.pdate.ruler.sample.service.RssItem
import jp.eno314.vcu.pdate.ruler.sample.service.RssService
import jp.eno314.vcu.pdate.ruler.sample.service.SiteInfo
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.OffsetDateTime

class RssControllerTest {
    private val rssService = mockk<RssService>()
    private val rssController = RssController(rssService)

    @Test
    fun `getRss returns response from service`() {
        // Given
        val request = RssFetchRequest(rss_url = "https://example.com/rss")
        val expectedResponse =
            RssFetchResponse(
                siteInfo =
                    SiteInfo(
                        title = "Test Title",
                        link = "https://example.com",
                        description = "Test Description",
                    ),
                items =
                    listOf(
                        RssItem(
                            id = "id1",
                            title = "item1",
                            link = "link1",
                            summaryHtml = "summary1",
                            publishedAt = OffsetDateTime.now(),
                            author = "author1",
                            thumbnailUrl = null,
                            categories = emptyList(),
                        ),
                    ),
            )
        every { rssService.fetchRss(request) } returns expectedResponse

        // When
        val response = rssController.getRss(request)

        // Then
        assertEquals(expectedResponse, response)
    }
}
