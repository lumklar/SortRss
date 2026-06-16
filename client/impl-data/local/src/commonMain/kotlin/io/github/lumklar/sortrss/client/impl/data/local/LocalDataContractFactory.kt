package io.github.lumklar.sortrss.client.impl.data.local

import io.github.lumklar.sortrss.client.contract.data.DataContractFactory
import io.github.lumklar.sortrss.common.domain.model.entity.User

class LocalDataContractFactory : DataContractFactory {
    override fun hello(): String {
        //TODO 删除
        val user = User.fromPersistence(12, "测试用户", "asdlfhalksdf")
        return "local-impl:" + user.id.value + "." + user.username
    }
}