package jp.eno314.vcu.pdate.ruler.sample.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jp.eno314.vcu.pdate.ruler.sample.service.RssFetchRequest
import jp.eno314.vcu.pdate.ruler.sample.service.RssFetchResponse
import jp.eno314.vcu.pdate.ruler.sample.service.RssService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rss")
@Tag(name = "RSS", description = "Operations related to RSS feeds")
class RssController(
    private val rssService: RssService,
) {
    @GetMapping
    @Operation(
        summary = "Fetch RSS feed",
        description = "Fetches and returns the contents of the specified RSS feed URL.",
    )
    @ApiResponse(responseCode = "200", description = "Successfully fetched the RSS feed")
    @ApiResponse(responseCode = "400", description = "Invalid request or RSS URL")
    fun getRss(
        @Valid request: RssFetchRequest,
    ): RssFetchResponse = rssService.fetchRss(request)
}
