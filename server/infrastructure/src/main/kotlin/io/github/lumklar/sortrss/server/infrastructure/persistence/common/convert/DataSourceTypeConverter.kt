package io.github.lumklar.sortrss.server.infrastructure.persistence.common.convert
import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter(autoApply = true)
@Component
class DataSourceTypeConverter : AttributeConverter<DataSourceType, Int> {

    override fun convertToDatabaseColumn(attribute: DataSourceType?): Int? {
        // 显式列出所有枚举值，不加 else → 新增枚举时此处编译错误（未覆盖）
        return when (attribute) {
            DataSourceType.LOCAL_OPML -> 0
            DataSourceType.FEVER_API -> 1
            DataSourceType.GOOGLE_READER_API -> 2
            null -> null
        }
    }

    override fun convertToEntityAttribute(dbData: Int?): DataSourceType? {
        // 从数据库整数还原为枚举，同样显式列出所有已知映射
        return when (dbData) {
            0 -> DataSourceType.LOCAL_OPML
            1 -> DataSourceType.FEVER_API
            2 -> DataSourceType.GOOGLE_READER_API
            null -> null
            else -> throw IllegalArgumentException("Unknown data source type code: $dbData")
        }
    }
}
