package jp.eno314.vcu.pdate.ruler.sample.repository

import io.mockk.every
import io.mockk.mockk
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssItemRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssRemoteResponse
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.SiteInfoRemoteResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.OffsetDateTime

class RssRepositoryTest {
    private val rssClient = mockk<RssClient>()
    private val rssRepository = RssRepository(rssClient)

    @Test
    fun `fetchRss maps RssRemoteResponse to RssDto`() {
        // Given
        val uri = URI.create("https://example.com/rss")
        val remoteResponse =
            RssRemoteResponse(
                siteInfo =
                    SiteInfoRemoteResponse(
                        title = "Remote Title",
                        link = "https://example.com",
                        description = "Remote Description",
                    ),
                items =
                    listOf(
                        RssItemRemoteResponse(
                            id = "remote-id",
                            title = "Remote Item",
                            link = "https://example.com/item",
                            summaryHtml = "Remote Summary",
                            publishedAt = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                            author = "Remote Author",
                            thumbnailUrl = "https://example.com/thumb.jpg",
                            categories = listOf("Category1"),
                        ),
                    ),
            )
        every { rssClient.fetch(uri) } returns remoteResponse

        // When
        val result = rssRepository.fetchRss(uri)

        // Then
        assertThat(result.siteInfo.title).isEqualTo("Remote Title")
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].title).isEqualTo("Remote Item")
    }
}
