package jp.eno314.vcu.pdate.ruler.sample.service

import jakarta.validation.constraints.NotBlank
import org.hibernate.validator.constraints.URL
import org.springframework.web.bind.annotation.BindParam

data class RssFetchRequest(
    @field:NotBlank
    @field:URL
    @BindParam("rss_url")
    val rssUrl: String?,
)
