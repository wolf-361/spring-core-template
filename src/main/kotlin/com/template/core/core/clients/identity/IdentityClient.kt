package com.template.core.core.clients.identity

import com.template.core.core.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClient
import java.util.UUID

@Component
class IdentityClient(
    private val appProperties: AppProperties
) {
    private val log = LoggerFactory.getLogger(javaClass)

    private val restClient by lazy {
        RestClient
            .builder()
            .baseUrl(appProperties.identity.url)
            .build()
    }

    fun getUserById(
        userId: UUID,
        bearerToken: String
    ): UserInfo? =
        try {
            restClient
                .get()
                .uri("/users/{id}/public", userId)
                .header("Authorization", bearerToken)
                .retrieve()
                .body(UserInfo::class.java)
        } catch (e: Exception) {
            log.warn("Failed to fetch user {} from identity service: {}", userId, e.javaClass.simpleName)
            null
        }
}