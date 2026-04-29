package jp.eno314.vcu.pdate.ruler.sample.repository

import java.time.OffsetDateTime

sealed interface RssFetchDto

data class Rss20FetchDto(
    val channel: Rss20ChannelDto,
    val items: List<Rss20ItemDto>,
) : RssFetchDto

data class Rss20ChannelDto(
    val title: String,
    val link: String,
    val description: String?,
)

data class Rss20ItemDto(
    val guid: String,
    val title: String,
    val link: String,
    val description: String,
    val pubDate: OffsetDateTime,
    val author: String?,
    val thumbnailUrl: String?,
    val categories: List<String>,
)

data class AtomFetchDto(
    val feed: AtomFeedDto,
    val entries: List<AtomEntryDto>,
) : RssFetchDto

data class AtomFeedDto(
    val title: String,
    val link: String,
    val subtitle: String?,
)

data class AtomEntryDto(
    val id: String,
    val title: String,
    val link: String,
    val summary: String,
    val published: OffsetDateTime,
    val author: String,
    val thumbnailUrl: String?,
    val categories: List<String>,
)
