package io.github.lumklar.sortrss.server.adaptor.web.api

import io.github.lumklar.sortrss.common.api.dto.ApiResult
import io.github.lumklar.sortrss.common.api.service.UserApi
import io.github.lumklar.sortrss.server.application.pojo.user.dto.UserDto
import io.github.lumklar.sortrss.server.application.pojo.user.query.UserQuery
import io.github.lumklar.sortrss.server.application.service.UserApplicationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userApplicationService: UserApplicationService
) : UserApi {
    /**
     * 根据用户名获取用户信息
     * TODO 在userapi中定义
     * @param username 用户名
     */
    @GetMapping("/api/users/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ApiResult<UserDto> {
        val query = UserQuery(username = username)
        val user = userApplicationService.queryUser(query)
        return ApiResult.success(user)
    }

}