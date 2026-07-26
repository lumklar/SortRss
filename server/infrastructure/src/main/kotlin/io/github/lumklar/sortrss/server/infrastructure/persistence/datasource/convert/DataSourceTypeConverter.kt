package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class DataSourceTypeConverter : AttributeConverter<DataSourceType, Int> {
    override fun convertToDatabaseColumn(attribute: DataSourceType?): Int? =
        attribute?.ordinal

    override fun convertToEntityAttribute(dbData: Int?): DataSourceType? =
        dbData?.let { DataSourceType.values()[it] }
}