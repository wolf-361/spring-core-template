package com.template.core.core.jwt

import com.template.core.core.config.AppProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import jakarta.servlet.FilterChain
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import java.util.Date
import java.util.UUID

class JwtValidationFilterTest {
    private lateinit var filter: JwtValidationFilter

    companion object {
        private const val TEST_SECRET = "test-secret-key-at-least-32-chars-long-ok"
    }

    @BeforeEach
    fun setup() {
        val props = mockk<AppProperties>()
        every { props.jwt } returns AppProperties.JwtProperties(secret = TEST_SECRET)
        filter = JwtValidationFilter(JwtValidator(props))
        SecurityContextHolder.clearContext()
    }

    private fun buildToken(
        userId: UUID,
        expiry: Date = Date(System.currentTimeMillis() + 300_000)
    ): String {
        val key = Keys.hmacShaKeyFor(TEST_SECRET.toByteArray())
        return Jwts
            .builder()
            .subject(userId.toString())
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    @Test
    fun `no Authorization header passes through unauthenticated`() {
        val req = MockHttpServletRequest()
        val res = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(req, res, chain)

        verify { chain.doFilter(req, res) }
        SecurityContextHolder.getContext().authentication shouldBe null
    }

    @Test
    fun `non-Bearer Authorization header passes through unauthenticated`() {
        val req = MockHttpServletRequest()
        req.addHeader("Authorization", "Basic dXNlcjpwYXNz")
        val res = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(req, res, chain)

        SecurityContextHolder.getContext().authentication shouldBe null
    }

    @Test
    fun `valid token sets UUID as authentication principal`() {
        val userId = UUID.randomUUID()
        val req = MockHttpServletRequest()
        req.addHeader("Authorization", "Bearer ${buildToken(userId)}")
        val res = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(req, res, chain)

        val auth = SecurityContextHolder.getContext().authentication
        auth?.principal shouldBe userId
    }

    @Test
    fun `expired token passes through unauthenticated`() {
        val userId = UUID.randomUUID()
        val token = buildToken(userId, expiry = Date(System.currentTimeMillis() - 1_000))
        val req = MockHttpServletRequest()
        req.addHeader("Authorization", "Bearer $token")
        val res = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(req, res, chain)

        SecurityContextHolder.getContext().authentication shouldBe null
    }

    @Test
    fun `already authenticated request skips JWT processing`() {
        val existing = UsernamePasswordAuthenticationToken("existing-principal", null, emptyList())
        SecurityContextHolder.getContext().authentication = existing

        val userId = UUID.randomUUID()
        val req = MockHttpServletRequest()
        req.addHeader("Authorization", "Bearer ${buildToken(userId)}")
        val res = MockHttpServletResponse()
        val chain = mockk<FilterChain>(relaxed = true)

        filter.doFilter(req, res, chain)

        SecurityContextHolder.getContext().authentication?.principal shouldBe "existing-principal"
    }
}