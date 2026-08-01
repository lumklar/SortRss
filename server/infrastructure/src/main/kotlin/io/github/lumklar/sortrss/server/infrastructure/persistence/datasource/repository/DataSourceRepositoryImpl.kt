package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSource
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceRepository
import io.github.lumklar.sortrss.common.domain.model.user.UserId
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository.jpa.DataSourceJpaRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import kotlin.uuid.toJavaUuid

@Repository
@Transactional
class DataSourceRepositoryImpl(
    private val jpaRepository: DataSourceJpaRepository
) : DataSourceRepository {

    override fun save(dataSource: DataSource): DataSource {
        val po = dataSource.toPO()
        val saved = jpaRepository.save(po)
        return saved.toDomain()
    }

    override fun findById(id: DataSourceId): DataSource? =
        jpaRepository.findById(id.value.toJavaUuid()).orElse(null)?.toDomain()

    override fun findByUserId(userId: UserId): List<DataSource> =
        jpaRepository.findByUserId(userId.value.toJavaUuid()).map { it.toDomain() }

    override fun delete(dataSource: DataSource) {
        jpaRepository.deleteById(dataSource.id.value.toJavaUuid())
    }
}