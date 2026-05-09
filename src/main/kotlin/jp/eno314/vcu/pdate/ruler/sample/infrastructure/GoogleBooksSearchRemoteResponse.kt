package jp.eno314.vcu.pdate.ruler.sample.infrastructure

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class GoogleBooksSearchRemoteResponse(
    val kind: String?,
    val totalItems: Int,
    val items: List<VolumeRemoteResponse>?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VolumeRemoteResponse(
    val id: String,
    val volumeInfo: VolumeInfoRemoteResponse,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class VolumeInfoRemoteResponse(
    val title: String,
    val authors: List<String>?,
    val publisher: String?,
    val publishedDate: String?,
    val description: String?,
    val imageLinks: ImageLinksRemoteResponse?,
    val infoLink: String?,
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ImageLinksRemoteResponse(
    val smallThumbnail: String?,
    val thumbnail: String?,
)
