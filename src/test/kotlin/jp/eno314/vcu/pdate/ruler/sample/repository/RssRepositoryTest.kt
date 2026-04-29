package jp.eno314.vcu.pdate.ruler.sample.repository

import io.mockk.every
import io.mockk.mockk
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteRequest
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.net.URI

class RssRepositoryTest {
    private val rssClient = mockk<RssClient>()
    private val rssRepository = RssRepository(rssClient)

    @Test
    fun `fetchRss should return Rss20FetchDto for RSS 2_0 format`() {
        // Arrange
        val uri = URI.create("https://example.com/rss")
        val dummyXml = """<rss version="2.0"></rss>"""
        every { rssClient.fetch(RssFetchRemoteRequest(uri)) } returns RssFetchRemoteResponse(dummyXml)

        // Act
        val dto = rssRepository.fetchRss(uri)

        // Assert
        assertThat(dto).isInstanceOf(Rss20FetchDto::class.java)
        val rss20Dto = dto as Rss20FetchDto
        assertThat(rss20Dto.channel.title).isEqualTo("サイトのタイトル")
    }

    @Test
    fun `fetchRss should return AtomFetchDto for Atom format`() {
        // Arrange
        val uri = URI.create("https://example.com/atom")
        val dummyXml = """<feed></feed>"""
        every { rssClient.fetch(RssFetchRemoteRequest(uri)) } returns RssFetchRemoteResponse(dummyXml)

        // Act
        val dto = rssRepository.fetchRss(uri)

        // Assert
        assertThat(dto).isInstanceOf(AtomFetchDto::class.java)
        val atomDto = dto as AtomFetchDto
        assertThat(atomDto.feed.title).isEqualTo("サイトのタイトル")
    }

    @Test
    fun `fetchRss should throw exception for unsupported format`() {
        // Arrange
        val uri = URI.create("https://example.com/unknown")
        val dummyXml = """<unknown></unknown>"""
        every { rssClient.fetch(RssFetchRemoteRequest(uri)) } returns RssFetchRemoteResponse(dummyXml)

        // Act & Assert
        val exception =
            assertThrows<IllegalArgumentException> {
                rssRepository.fetchRss(uri)
            }
        assertThat(exception.message).isEqualTo("Unsupported RSS format")
    }
}
