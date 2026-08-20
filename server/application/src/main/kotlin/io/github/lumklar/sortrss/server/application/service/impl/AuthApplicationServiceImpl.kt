package io.github.lumklar.sortrss.server.application.service.impl

import io.github.lumklar.sortrss.common.domain.model.user.ExternalProvider
import io.github.lumklar.sortrss.common.domain.service.ExternalAccountService
import io.github.lumklar.sortrss.server.application.oauth.OAuthClient
import io.github.lumklar.sortrss.server.application.service.AuthApplicationService
import org.springframework.stereotype.Service

@Service
class AuthApplicationServiceImpl(
    private val oauthClients: Map<ExternalProvider, OAuthClient>,
    private val externalAccountService: ExternalAccountService,
) : AuthApplicationService {

}