package io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository

import io.github.lumklar.sortrss.common.domain.model.datasource.DataSourceId
import io.github.lumklar.sortrss.common.domain.model.folder.Folder
import io.github.lumklar.sortrss.common.domain.model.folder.FolderId
import io.github.lumklar.sortrss.common.domain.model.folder.FolderRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.convert.toDomain
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.convert.toPO
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderClosureId
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.entity.FolderClosurePO
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa.JpaFolderClosureRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa.JpaFolderMembershipRepository
import io.github.lumklar.sortrss.server.infrastructure.persistence.folder.repository.jpa.JpaFolderRepository
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional
import java.util.*
import kotlin.uuid.toJavaUuid

@Repository
class FolderRepositoryImpl(
    private val jpaFolderRepo: JpaFolderRepository,
    private val jpaClosureRepo: JpaFolderClosureRepository,
    private val jpaMembershipRepo: JpaFolderMembershipRepository
) : FolderRepository {

    @Transactional
    override fun save(folder: Folder): Folder {
        val po = folder.toPO()
        // 先保存 FolderPO
        val savedPo = jpaFolderRepo.save(po)

        val id = savedPo.id ?: throw IllegalStateException("ID must not be null after save")

        // 重建该节点及其所有后代的闭包
        rebuildClosure(id)

        return savedPo.toDomain()
    }

    override fun findById(id: FolderId): Folder? {
        return jpaFolderRepo.findById(id.value.toJavaUuid())
            .map { it.toDomain() }
            .orElse(null)
    }

    override fun findByDataSourceId(dataSourceId: DataSourceId): List<Folder> {
        return jpaFolderRepo.findByDataSourceId(dataSourceId.value.toJavaUuid())
            .map { it.toDomain() }
    }

    @Transactional
    override fun delete(id: FolderId) {
        val folderId = id.value.toJavaUuid()

        // 1. 获取该文件夹及其所有后代（含自身）
        val descendants = jpaClosureRepo.findDescendantsByAncestor(folderId).toMutableList()
        if (folderId !in descendants) descendants.add(folderId) // 确保自身在列表

        // 2. 删除这些文件夹下的所有 membership
        jpaMembershipRepo.deleteByFolderId(folderId) // 只删除该节点的 membership？但需要删除所有后代的，所以应使用 in 查询。
        // 改为批量删除
        jpaMembershipRepo.deleteAllByFolderIdIn(descendants) // 自定义方法需添加

        // 3. 删除闭包表中所有涉及这些节点的记录
        jpaClosureRepo.deleteByAncestorInOrDescendantIn(descendants)

        // 4. 删除 FolderPO
        jpaFolderRepo.deleteAllById(descendants)
    }

    // ---------- 闭包维护辅助方法 ----------

    /**
     * 重建以 rootId 为根的整棵子树的闭包。
     * 适用于新建或移动文件夹后的更新。
     */
    private fun rebuildClosure(rootId: UUID) {
        // 1. 获取所有后代（含自身）
        val descendants = jpaClosureRepo.findDescendantsByAncestor(rootId).toMutableSet()
        if (rootId !in descendants) descendants.add(rootId)

        // 2. 删除旧闭包（所有以这些节点为后代的记录）
        jpaClosureRepo.deleteByDescendantIn(descendants.toList())

        // 3. 缓存父级查询结果（避免多次查询同一节点）
        val parentCache = mutableMapOf<UUID, UUID?>()
        fun getParentId(id: UUID): UUID? = parentCache.getOrPut(id) {
            jpaFolderRepo.findById(id).map { it.parentFolderId }.orElse(null)
        }

        // 4. 为每个后代重新生成完整祖先链
        val newClosures = mutableListOf<FolderClosurePO>()
        for (descendantId in descendants) {
            var current: UUID? = descendantId
            var depth = 0
            while (current != null) {
                newClosures.add(
                    FolderClosurePO().apply {
                        id = FolderClosureId(current, descendantId)
                        this.depth = depth
                    }
                )
                current = getParentId(current)
                depth++
            }
        }

        // 5. 批量保存新闭包
        jpaClosureRepo.saveAll(newClosures)
    }
}
