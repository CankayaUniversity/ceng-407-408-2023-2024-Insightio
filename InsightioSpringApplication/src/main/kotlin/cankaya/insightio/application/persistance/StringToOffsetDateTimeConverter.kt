package cankaya.insightio.application.persistance

import org.springframework.core.convert.converter.Converter
import java.time.*
import java.time.format.DateTimeFormatter

class StringToOffsetDateTimeConverter : Converter<String, OffsetDateTime> {
    override fun convert(source: String): OffsetDateTime {
        return OffsetDateTime.parse(source, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    companion object {
        fun generateISO8601DateTime(): String {
            val currentDateTime = OffsetDateTime.now(ZoneOffset.UTC)
            val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
            return currentDateTime.format(formatter)
        }
    }
}

class StringToInstantConverter : Converter<String, Instant> {
    override fun convert(source: String): Instant {
        return Instant.parse(source)
    }
}

class StringToZonedDateTimeConverter : Converter<String, ZonedDateTime> {
    override fun convert(source: String): ZonedDateTime {
        return Instant.parse(source).atZone(ZoneOffset.UTC)
    }
}

class InstantToLocalDateConverter : Converter<Instant, LocalDate> {
    override fun convert(source: Instant): LocalDate? {
        return LocalDate.from(source.atZone(ZoneOffset.UTC))
    }
}
