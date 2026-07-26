package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert

import com.alibaba.fastjson2.JSON
import com.alibaba.fastjson2.JSONReader
import com.alibaba.fastjson2.JSONWriter
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.springframework.stereotype.Component

@Converter
@Component
class DataSourceConnectionDetailsConverter : AttributeConverter<DataSourceConnectionDetails, String> {
    override fun convertToDatabaseColumn(attribute: DataSourceConnectionDetails?): String? {
        if (attribute == null) return null
        // 序列化时写入类型信息（@type 字段）
        return JSON.toJSONString(attribute, JSONWriter.Feature.WriteClassName)
    }

    override fun convertToEntityAttribute(dbData: String?): DataSourceConnectionDetails? {
        if (dbData == null) return null
        // 反序列化时支持 AutoType
        return JSON.parseObject(dbData, DataSourceConnectionDetails::class.java,
            JSONReader.Feature.SupportAutoType
        )
    }
}