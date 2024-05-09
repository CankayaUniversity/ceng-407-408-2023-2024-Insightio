package cankaya.insightio.domain

data class CountReportAudit(
    val target: String,
    val counts: List<List<Int>>,
)
