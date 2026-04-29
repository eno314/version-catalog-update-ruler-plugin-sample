package jp.eno314.vcu.pdate.ruler.sample.service

import io.mockk.every
import io.mockk.mockk
import jp.eno314.vcu.pdate.ruler.sample.repository.RssDto
import jp.eno314.vcu.pdate.ruler.sample.repository.RssItemDto
import jp.eno314.vcu.pdate.ruler.sample.repository.RssRepository
import jp.eno314.vcu.pdate.ruler.sample.repository.SiteInfoDto
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI
import java.time.OffsetDateTime

class RssServiceTest {
    private val rssRepository = mockk<RssRepository>()
    private val rssService = RssService(rssRepository)

    @Test
    fun `fetchRss maps RssDto to RssFetchResponse and converts URL to URI`() {
        // Given
        val rssUrl = "https://example.com/rss"
        val request = RssFetchRequest(rssUrl = rssUrl)
        val rssDto =
            RssDto(
                siteInfo =
                    SiteInfoDto(
                        title = "Dto Title",
                        link = "https://example.com",
                        description = "Dto Description",
                    ),
                items =
                    listOf(
                        RssItemDto(
                            id = "dto-id",
                            title = "Dto Item",
                            link = "https://example.com/item",
                            summaryHtml = "Dto Summary",
                            publishedAt = OffsetDateTime.parse("2026-04-25T10:00:00Z"),
                            author = "Dto Author",
                            thumbnailUrl = null,
                            categories = emptyList(),
                        ),
                    ),
            )
        every { rssRepository.fetchRss(URI.create(rssUrl)) } returns rssDto

        // When
        val result = rssService.fetchRss(request)

        // Then
        assertThat(result.siteInfo.title).isEqualTo("Dto Title")
        assertThat(result.items).hasSize(1)
        assertThat(result.items[0].title).isEqualTo("Dto Item")
    }
}
