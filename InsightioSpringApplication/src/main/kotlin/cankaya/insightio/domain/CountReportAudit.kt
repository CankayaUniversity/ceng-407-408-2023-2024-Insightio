package cankaya.insightio.domain

data class CountReportAudit(
    val target: Int,
    val counts: List<List<Int>>,
)
