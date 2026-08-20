package io.github.lumklar.sortrss.server.application.oauth

import io.github.lumklar.sortrss.common.domain.model.user.ExternalProvider

interface OAuthClient {
    val provider: ExternalProvider
    suspend fun getSubject(authCode: String): String
}
