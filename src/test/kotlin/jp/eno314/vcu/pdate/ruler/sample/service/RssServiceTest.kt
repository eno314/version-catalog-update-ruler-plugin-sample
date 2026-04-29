package jp.eno314.vcu.pdate.ruler.sample.service

import io.mockk.every
import io.mockk.mockk
import jp.eno314.vcu.pdate.ruler.sample.repository.AtomFeedDto
import jp.eno314.vcu.pdate.ruler.sample.repository.AtomFetchDto
import jp.eno314.vcu.pdate.ruler.sample.repository.Rss20ChannelDto
import jp.eno314.vcu.pdate.ruler.sample.repository.Rss20FetchDto
import jp.eno314.vcu.pdate.ruler.sample.repository.RssRepository
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.net.URI

class RssServiceTest {
    private val rssRepository = mockk<RssRepository>()
    private val rssService = RssService(rssRepository)

    @Test
    fun `fetchRss should map Rss20FetchDto to RssFetchResponse`() {
        // Arrange
        val request = RssFetchRequest(rssUrl = "https://example.com/rss")
        val uri = URI.create("https://example.com/rss")
        val dummyDto =
            Rss20FetchDto(
                channel = Rss20ChannelDto("RSS Title", "https://rss.link", "RSS Desc"),
                items = emptyList(),
            )
        every { rssRepository.fetchRss(uri) } returns dummyDto

        // Act
        val response = rssService.fetchRss(request)

        // Assert
        assertThat(response.siteInfo.title).isEqualTo("RSS Title")
        assertThat(response.siteInfo.link).isEqualTo("https://rss.link")
        assertThat(response.siteInfo.description).isEqualTo("RSS Desc")
        assertThat(response.items).isEmpty()
    }

    @Test
    fun `fetchRss should map AtomFetchDto to RssFetchResponse`() {
        // Arrange
        val request = RssFetchRequest(rssUrl = "https://example.com/atom")
        val uri = URI.create("https://example.com/atom")
        val dummyDto =
            AtomFetchDto(
                feed = AtomFeedDto("Atom Title", "https://atom.link", "Atom Desc"),
                entries = emptyList(),
            )
        every { rssRepository.fetchRss(uri) } returns dummyDto

        // Act
        val response = rssService.fetchRss(request)

        // Assert
        assertThat(response.siteInfo.title).isEqualTo("Atom Title")
        assertThat(response.siteInfo.link).isEqualTo("https://atom.link")
        assertThat(response.siteInfo.description).isEqualTo("Atom Desc")
        assertThat(response.items).isEmpty()
    }
}
