package io.github.lumklar.sortrss.server.application.assembler

import io.github.lumklar.sortrss.common.domain.shared.enums.DataSourceType
import io.github.lumklar.sortrss.server.application.pojo.datasource.dto.DataSourceTypeDto

fun DataSourceTypeDto.toDomainType(): DataSourceType = when (this) {
    DataSourceTypeDto.LOCAL_OPML -> DataSourceType.LOCAL_OPML
    DataSourceTypeDto.FEVER_API -> DataSourceType.FEVER_API
    DataSourceTypeDto.GOOGLE_READER_API -> DataSourceType.GOOGLE_READER_API
}

fun DataSourceType.toDtoType(): DataSourceTypeDto = when (this) {
    DataSourceType.LOCAL_OPML -> DataSourceTypeDto.LOCAL_OPML
    DataSourceType.FEVER_API -> DataSourceTypeDto.FEVER_API
    DataSourceType.GOOGLE_READER_API -> DataSourceTypeDto.GOOGLE_READER_API
}
