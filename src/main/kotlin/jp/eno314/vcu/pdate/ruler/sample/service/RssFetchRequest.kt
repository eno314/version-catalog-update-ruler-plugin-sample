package jp.eno314.vcu.pdate.ruler.sample.service

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL

data class RssFetchRequest(
    @field:NotBlank
    @field:URL
    @Schema(description = "The URL of the RSS feed to fetch", example = "https://example.com/rss", required = true)
    val rssUrl: String,
)
