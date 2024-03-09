package cankaya.insightio.application.services

import cankaya.insightio.infrastructure.mongodb.impls.CountRepository
import org.springframework.stereotype.Service

@Service
class CountService(private val countRepository: CountRepository)
