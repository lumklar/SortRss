package io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.repository.jpa

import io.github.lumklar.sortrss.server.infrastructure.persistence.datasource.entity.DataSourcePo
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface DataSourceJpaRepository : JpaRepository<DataSourcePo, UUID> {
    fun findByUserId(userId: UUID): List<DataSourcePo>
}
