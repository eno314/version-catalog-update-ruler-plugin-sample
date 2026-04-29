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
    fun fetchRss(uri: URI): RssFetchDto {
        val remoteRequest = RssFetchRemoteRequest(uri = uri)
        val remoteResponse = rssClient.fetch(remoteRequest)

        return try {
            when {
                remoteResponse.rawXml.contains("<rss version=\"2.0\">") -> rssParser.parseRss20(remoteResponse.rawXml)
                remoteResponse.rawXml.contains("<feed") -> rssParser.parseAtom(remoteResponse.rawXml)
                else -> throw IllegalArgumentException("Unsupported RSS format")
            }
        } catch (e: Exception) {
            throw IllegalArgumentException("Unsupported RSS format", e)
        }
    }
}
