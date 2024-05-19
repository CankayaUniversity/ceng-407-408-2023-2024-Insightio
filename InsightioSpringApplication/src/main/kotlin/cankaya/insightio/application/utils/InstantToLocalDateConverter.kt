package cankaya.insightio.application.utils

import org.springframework.core.convert.converter.Converter
import java.time.*

class InstantToLocalDateConverter : Converter<Instant, LocalDate> {
    override fun convert(source: Instant): LocalDate {
        return LocalDate.from(source.atZone(ZoneOffset.UTC))
    }
}
