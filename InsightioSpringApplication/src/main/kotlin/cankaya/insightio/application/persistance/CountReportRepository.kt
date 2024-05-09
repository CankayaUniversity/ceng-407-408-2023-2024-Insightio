package cankaya.insightio.application.persistance

import cankaya.insightio.domain.CountReport

interface CountReportRepository {
    fun save(countReport: CountReport): CountReport
}
