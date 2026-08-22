package io.github.lumklar.sortrss.server.application.pojo.datasource.dto

data class DataSourceDTO(
    val id: String,
    val userId: String,
    val type: DataSourceTypeDto,
)
