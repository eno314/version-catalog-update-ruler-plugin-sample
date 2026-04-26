package jp.eno314.vcu.pdate.ruler.sample.controller

import jakarta.validation.Valid
import jp.eno314.vcu.pdate.ruler.sample.service.RssFetchRequest
import jp.eno314.vcu.pdate.ruler.sample.service.RssFetchResponse
import jp.eno314.vcu.pdate.ruler.sample.service.RssService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/rss")
class RssController(
    private val rssService: RssService
) {
    @GetMapping
    fun getRss(@Valid request: RssFetchRequest): RssFetchResponse {
        return rssService.fetchRss(request)
    }
}
