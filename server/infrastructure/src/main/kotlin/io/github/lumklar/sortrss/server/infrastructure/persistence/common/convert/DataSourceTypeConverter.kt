package io.github.lumklar.sortrss.server.infrastructure.persistence.common.convert
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
class DataSourceTypeConverter : AttributeConverter<DataSourceType, String> {

    private companion object {
        const val LOCAL_OPML = "LOCAL_OPML"
        const val FEVER_API = "FEVER_API"
        const val GOOGLE_READER_API = "GOOGLE_READER_API"
    }

    override fun convertToDatabaseColumn(attribute: DataSourceType?): String? {
        return when (attribute) {
            DataSourceType.LOCAL_OPML -> LOCAL_OPML
            DataSourceType.FEVER_API -> FEVER_API
            DataSourceType.GOOGLE_READER_API -> GOOGLE_READER_API
            null -> null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): DataSourceType? {
        return when (dbData) {
            LOCAL_OPML -> DataSourceType.LOCAL_OPML
            FEVER_API -> DataSourceType.FEVER_API
            GOOGLE_READER_API -> DataSourceType.GOOGLE_READER_API
            null -> null
            else -> throw IllegalArgumentException("Unknown data source type: $dbData")
        }
    }
}
