package io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert

import io.github.lumklar.sortrss.common.domain.model.user.RegistrationSource
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class RegistrationSourceConverter : AttributeConverter<RegistrationSource, String> {

    // 私有常量，仅转换器内部使用
    private companion object {
        const val LOCAL = "LOCAL"
        const val EXTERNAL = "EXTERNAL"
        const val ANONYMOUS = "ANONYMOUS"
    }

    override fun convertToDatabaseColumn(attribute: RegistrationSource?): String? {
        return when (attribute) {
            RegistrationSource.LOCAL -> LOCAL
            RegistrationSource.EXTERNAL -> EXTERNAL
            RegistrationSource.ANONYMOUS -> ANONYMOUS
            null -> null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): RegistrationSource? {
        return when (dbData) {
            LOCAL -> RegistrationSource.LOCAL
            EXTERNAL -> RegistrationSource.EXTERNAL
            ANONYMOUS -> RegistrationSource.ANONYMOUS
            null -> null
            else -> throw IllegalArgumentException("Unknown registration source: $dbData")
        }
    }
}
