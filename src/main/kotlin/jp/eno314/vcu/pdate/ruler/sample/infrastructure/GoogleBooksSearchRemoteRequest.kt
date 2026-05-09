package jp.eno314.vcu.pdate.ruler.sample.infrastructure

data class GoogleBooksSearchRemoteRequest(
    val query: String,
    val googleBooksApiKey: String,
    val printType: String,
    val langRestrict: String,
)
