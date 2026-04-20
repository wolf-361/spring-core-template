package com.template.core.core.jwt

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtValidationFilter(
    private val jwtValidator: JwtValidator
) : OncePerRequestFilter() {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        try {
            val token = authHeader.substring(7)

            if (jwtValidator.isValid(token) &&
                SecurityContextHolder.getContext().authentication == null
            ) {
                val userId = jwtValidator.extractUserId(token)
                val auth = UsernamePasswordAuthenticationToken(userId, null, emptyList())
                auth.details = WebAuthenticationDetailsSource().buildDetails(request)
                SecurityContextHolder.getContext().authentication = auth
                MDC.put("userId", userId.toString())
            }
        } catch (e: Exception) {
            log.warn("JWT processing failed: ${e.javaClass.simpleName} — proceeding unauthenticated")
        }

        try {
            filterChain.doFilter(request, response)
        } finally {
            MDC.remove("userId")
        }
    }
}