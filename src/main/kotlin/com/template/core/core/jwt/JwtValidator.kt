package com.template.core.core.jwt

import com.template.core.core.config.AppProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import java.util.UUID
import javax.crypto.SecretKey

@Component
class JwtValidator(
    private val appProperties: AppProperties
) {
    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(appProperties.jwt.secret.toByteArray())
    }

    fun isValid(token: String): Boolean =
        try {
            !parseClaims(token).expiration.before(Date())
        } catch (e: JwtException) {
            false
        } catch (e: IllegalArgumentException) {
            false
        }

    fun extractUserId(token: String): UUID = UUID.fromString(parseClaims(token).subject)

    private fun parseClaims(token: String): Claims =
        Jwts
            .parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
}