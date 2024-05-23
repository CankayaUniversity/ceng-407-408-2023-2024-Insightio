package cankaya.insightio.application.utils

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
