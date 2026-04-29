package jp.eno314.vcu.pdate.ruler.sample.repository

import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteRequest
import org.springframework.stereotype.Repository
import java.net.URI

@Repository
class RssRepository(
    private val rssClient: RssClient,
    private val rssParser: RssParser,
) {
    private fun createXmlParseError(e: Exception) = IllegalArgumentException("Unsupported RSS format", e)

    @Suppress("ThrowsCount")
    private fun parseRssSafely(xmlString: String): RssFetchDto =
        try {
            when {
                xmlString.contains(Regex("<rss[^>]*version=\"2.0\"")) ->
                    rssParser.parseRss20(xmlString)

                xmlString.contains("<feed") ->
                    rssParser.parseAtom(xmlString)

                else -> throw IllegalArgumentException("Unsupported RSS format")
            }
        } catch (e: org.xml.sax.SAXException) {
            throw createXmlParseError(e)
        } catch (e: javax.xml.parsers.ParserConfigurationException) {
            throw createXmlParseError(e)
        } catch (e: java.io.IOException) {
            throw createXmlParseError(e)
        } catch (e: javax.xml.xpath.XPathException) {
            throw createXmlParseError(e)
        }

    private fun parseRssWithExceptionHandling(xmlString: String): RssFetchDto = parseRssSafely(xmlString)

    fun fetchRss(uri: URI): RssFetchDto {
        val remoteRequest = RssFetchRemoteRequest(uri = uri)
        val remoteResponse = rssClient.fetch(remoteRequest)

        return parseRssWithExceptionHandling(remoteResponse.rawXml)
    }
}
