package io.github.lumklar.sortrss.server.adaptor.web.api

import io.github.lumklar.sortrss.common.api.dto.ApiResult
import io.github.lumklar.sortrss.common.api.dto.user.UserDto
import io.github.lumklar.sortrss.common.api.service.UserApi
import io.github.lumklar.sortrss.server.application.pojo.user.query.UserQuery
import io.github.lumklar.sortrss.server.application.service.UserApplicationService
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userApplicationService: UserApplicationService
) : UserApi {
    override suspend fun getUserByUsername(@PathVariable username: String): ApiResult<UserDto> {
        val query = UserQuery(username = username)
        val user = userApplicationService.queryUser(query)
        return ApiResult.success(user)
    }
}
