// ==================== Group 实体 ====================
package io.github.lumklar.sortrss.common.domain.model.entity

import io.github.lumklar.sortrss.common.domain.shared.valueobject.DataSourceId
import io.github.lumklar.sortrss.common.domain.shared.valueobject.GroupId

/**
 * 分组，属于特定数据源。
 */
class Group private constructor(
    val id: GroupId,
    val dataSourceId: DataSourceId,
    var name: String,
    var displayOrder: Int
) {
    companion object {
        fun create(
            dataSourceId: DataSourceId,
            name: String,
            displayOrder: Int = 0,
            id: GroupId = GroupId(0)
        ): Group {
            require(name.isNotBlank()) { "分组名称不能为空" }
            return Group(id, dataSourceId, name.trim(), displayOrder)
        }
    }

    fun rename(newName: String) {
        require(newName.isNotBlank()) { "分组名称不能为空" }
        name = newName.trim()
    }

    fun updateOrder(order: Int) {
        displayOrder = order
    }
}