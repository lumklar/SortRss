package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSource
import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceConnectionDetails
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
        po.uniqueKey = generateUniqueKey(dataSource.connectionDetails)
        val saved = jpaRepository.save(po)
        return saved.toDomain()
    }

    override fun findById(id: DataSourceId): DataSource? =
        jpaRepository.findById(id.value.toJavaUuid()).orElse(null)?.toDomain()

    override fun findByUserId(userId: UserId): List<DataSource> =
        jpaRepository.findByUserId(userId.value.toJavaUuid()).map { it.toDomain() }

    override fun findByConnectionDetails(details: DataSourceConnectionDetails): DataSource? {
        val uniqueKey = generateUniqueKey(details)
        return jpaRepository.findByUniqueKey(uniqueKey)?.toDomain()
    }

    override fun existsByConnectionDetails(details: DataSourceConnectionDetails): Boolean {
        val uniqueKey = generateUniqueKey(details)
        return jpaRepository.existsByUniqueKey(uniqueKey)
    }

    override fun delete(dataSource: DataSource) {
        jpaRepository.deleteById(dataSource.id.value.toJavaUuid())
    }

    /**
     * 根据连接详情生成稳定唯一键，仅用于数据库优化。
     * 本地 OPML 没有稳定标识，返回随机 UUID 保证数据库列非空且唯一，
     * 但业务上不限制重复（existsByConnectionDetails 对 LocalOpml 应返回 false 或特殊处理）。
     */
    private fun generateUniqueKey(details: DataSourceConnectionDetails): String {
        return when (details) {
            is DataSourceConnectionDetails.LocalOpml -> details.datasourceId.value.toString()
            is DataSourceConnectionDetails.FeverApi ->
                "fever:${details.endpoint.value.trimEnd('/')}:${details.username}"
            is DataSourceConnectionDetails.GoogleReaderApi ->
                "google-reader:${details.endpoint.value.trimEnd('/')}:${details.accessToken}"
        }
    }
}