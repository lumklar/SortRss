package io.github.lumklar.sortrss.server.infrastructure.persistence.user.convert

import io.github.lumklar.sortrss.common.domain.model.user.ExternalProvider
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter

@Converter(autoApply = true)
class ExternalProviderConverter : AttributeConverter<ExternalProvider, String> {

    private companion object {
        const val QQ = "QQ"
        const val WECHAT = "WECHAT"
        const val WEIBO = "WEIBO"
        const val GOOGLE = "GOOGLE"
    }

    override fun convertToDatabaseColumn(attribute: ExternalProvider?): String? {
        return when (attribute) {
            ExternalProvider.QQ -> QQ
            ExternalProvider.WECHAT -> WECHAT
            ExternalProvider.WEIBO -> WEIBO
            ExternalProvider.GOOGLE -> GOOGLE
            null -> null
        }
    }

    override fun convertToEntityAttribute(dbData: String?): ExternalProvider? {
        return when (dbData) {
            QQ -> ExternalProvider.QQ
            WECHAT -> ExternalProvider.WECHAT
            WEIBO -> ExternalProvider.WEIBO
            GOOGLE -> ExternalProvider.GOOGLE
            null -> null
            else -> throw IllegalArgumentException("Unknown external provider: $dbData")
        }
    }
}
