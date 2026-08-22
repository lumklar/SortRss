package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import com.fasterxml.jackson.annotation.JsonSubTypes
import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.KotlinModule
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import io.github.lumklar.sortrss.common.foundation.model.datasource.FeverApiConnectionDetails
import io.github.lumklar.sortrss.common.foundation.model.datasource.GoogleReaderApiConnectionDetails
import io.github.lumklar.sortrss.common.foundation.model.datasource.LocalOpmlConnectionDetails
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter
@Component
class DataSourceConnectionDetailsConverter :
    AttributeConverter<DataSourceConnectionDetails, String> {

    override fun convertToDatabaseColumn(
        attribute: DataSourceConnectionDetails?
    ): String? {
        return attribute?.let(OBJECT_MAPPER::writeValueAsString)
    }

    override fun convertToEntityAttribute(
        dbData: String?
    ): DataSourceConnectionDetails? {
        if (dbData.isNullOrBlank()) {
            return null
        }

        return OBJECT_MAPPER.readValue(
            dbData,
            DataSourceConnectionDetails::class.java
        )
    }

    companion object {
        private val OBJECT_MAPPER = JsonMapper.builder()
            .addModule(
                KotlinModule.Builder()
                    // 保证 Kotlin object/data object 反序列化后仍使用单例实例
                    .enable(KotlinFeature.SingletonSupport)
                    .build()
            )
            // LocalOpml 没有业务字段，允许其序列化为空对象并携带 type
            .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
            .build()
            .apply {
                addMixIn(
                    DataSourceConnectionDetails::class.java,
                    DataSourceConnectionDetailsMixin::class.java
                )
            }
    }
}

/**
 * 通过 MixIn 为领域对象提供 Jackson 多态配置，
 * 避免领域模型依赖 Jackson API。
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "type"
)
@JsonSubTypes(
    JsonSubTypes.Type(value = LocalOpmlConnectionDetails::class, name = "local-opml"),
    JsonSubTypes.Type(value = FeverApiConnectionDetails::class, name = "fever-api"),
    JsonSubTypes.Type(value = GoogleReaderApiConnectionDetails::class, name = "google-reader-api")
)
private abstract class DataSourceConnectionDetailsMixin
