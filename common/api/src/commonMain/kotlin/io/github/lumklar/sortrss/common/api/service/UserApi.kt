package io.github.lumklar.sortrss.common.api.service

import io.github.lumklar.sortrss.common.api.annotation.ApiRoute
import io.github.lumklar.sortrss.common.api.annotation.HttpMethod
import io.github.lumklar.sortrss.common.api.dto.ApiResult
import io.github.lumklar.sortrss.common.api.dto.user.UserDto
import io.github.lumklar.sortrss.common.api.route.UserRoute

/**
 * 用户相关API
 */
interface UserApi {
    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    @ApiRoute(UserRoute.USER_BY_USERNAME, HttpMethod.GET)
    suspend fun getUserByUsername(username: String): ApiResult<UserDto>
}
