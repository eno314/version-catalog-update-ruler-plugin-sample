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
    private val rssParser = mockk<RssParser>()
    private val rssRepository = RssRepository(rssClient, rssParser)

    @Test
    fun `fetchRss should return Rss20FetchDto for RSS 2_0 format`() {
        // Arrange
        val uri = URI.create("https://example.com/rss")
        val dummyXml = """<rss version="2.0"></rss>"""
        val expectedDto = Rss20FetchDto(
            channel = Rss20ChannelDto(title = "サイトのタイトル", link = "https://example.com", description = "サイトの概要説明"),
            items = emptyList(),
        )
        every { rssClient.fetch(RssFetchRemoteRequest(uri)) } returns RssFetchRemoteResponse(dummyXml)
        every { rssParser.parseRss20(dummyXml) } returns expectedDto

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
        val expectedDto = AtomFetchDto(
            feed = AtomFeedDto(title = "サイトのタイトル", link = "https://example.com", subtitle = "サイトの概要説明"),
            entries = emptyList(),
        )
        every { rssClient.fetch(RssFetchRemoteRequest(uri)) } returns RssFetchRemoteResponse(dummyXml)
        every { rssParser.parseAtom(dummyXml) } returns expectedDto

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
