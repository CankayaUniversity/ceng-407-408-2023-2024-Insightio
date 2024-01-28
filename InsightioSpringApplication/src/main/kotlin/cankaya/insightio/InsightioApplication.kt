package cankaya.insightio

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer


@SpringBootApplication
class InsightioApplication: SpringBootServletInitializer()

// en dışarıdadır
// bu directory de domain application ve infra vardır
// domain de sadece domain objeleri olur app ve infarnın varlığını bilmez: Human objesi vs orada tanımlanır


// application ->  infra nın varlığını bilmez, domain objelerini kullanarak servis leri implemente eder
// mesela script i ayağa kaldırma servisi, account management servisi

// infra -> 3rd party dependencies yaşar: couchbase http client lar (başka yere istek attıkların), swagger falan buraya koyucam
// burası da domain objelerini ve application servislerini kullanır
// içinde controllerlar var


fun main(args: Array<String>) {
	// uygulama buradan ayağa kalkar
	runApplication<InsightioApplication>(*args)
}
