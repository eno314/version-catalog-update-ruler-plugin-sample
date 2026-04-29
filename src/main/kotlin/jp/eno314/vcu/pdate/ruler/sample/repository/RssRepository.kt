package jp.eno314.vcu.pdate.ruler.sample.repository

import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssClient
import jp.eno314.vcu.pdate.ruler.sample.infrastructure.RssFetchRemoteRequest
import org.springframework.stereotype.Repository
import java.net.URI

@Repository
class RssRepository(
    private val rssClient: RssClient,
    private val rss20Parser: Rss20Parser,
    private val rssAtomParser: RssAtomParser,
) {
    fun fetchRss(uri: URI): RssFetchDto {
        val remoteRequest = RssFetchRemoteRequest(uri = uri)
        val remoteResponse = rssClient.fetch(remoteRequest)
        return parseRssSafely(remoteResponse.rawXml)
    }

    private fun parseRssSafely(xmlString: String): RssFetchDto =
        when {
            xmlString.contains(Regex("<rss[^>]*version=\"2.0\"")) ->
                rss20Parser.parseRss20(xmlString)

            xmlString.contains("<feed") ->
                rssAtomParser.parseAtom(xmlString)

            else -> throw IllegalArgumentException("Unsupported RSS format")
        }
}
