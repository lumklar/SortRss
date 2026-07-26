package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component
import kotlin.time.Instant

@Converter(autoApply = true)
@Component
class InstantConverter : AttributeConverter<Instant, Long> {
    override fun convertToDatabaseColumn(attribute: Instant?): Long? =
        attribute?.toEpochMilliseconds()

    override fun convertToEntityAttribute(dbData: Long?): Instant? =
        dbData?.let { Instant.fromEpochMilliseconds(it) }
}