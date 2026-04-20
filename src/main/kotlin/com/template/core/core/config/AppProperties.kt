package com.template.core.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app")
data class AppProperties(
    val cors: CorsProperties,
    val jwt: JwtProperties,
    val identity: IdentityProperties = IdentityProperties()
) {
    data class CorsProperties(
        val allowedOrigins: List<String> = emptyList()
    )

    data class JwtProperties(
        val secret: String
    )

    data class IdentityProperties(
        val url: String = "http://localhost:8080"
    )
}