package com.template.core.core.exception

import com.template.core.features.item.domain.exception.ItemException
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = LoggerFactory.getLogger(javaClass)

    private fun sanitizeForLog(value: String?): String =
        value
            ?.replace(Regex("[\\r\\n\\t\\u0000-\\u001F\\u007F]"), "_")
            ?: "null"

    @ExceptionHandler(ItemException.NotFound::class)
    fun handleItemNotFound(
        ex: ItemException,
        req: HttpServletRequest
    ) = error(HttpStatus.NOT_FOUND, "ITEM_NOT_FOUND", "Item not found")
        .also { log.warn("Item not found [{}]", sanitizeForLog(req.requestURI)) }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val field = ex.bindingResult.fieldErrors.firstOrNull()
        val message = if (field != null) "${field.field}: ${field.defaultMessage}" else "Validation failed"
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", message)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleNotReadable(ex: HttpMessageNotReadableException) =
        error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Malformed or missing request body")

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgument(ex: IllegalArgumentException) =
        error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.message ?: "Bad request")

    @ExceptionHandler(Exception::class)
    fun handleGeneric(
        ex: Exception,
        req: HttpServletRequest
    ): ResponseEntity<ErrorResponse> {
        log.error("Unexpected error [{}]", sanitizeForLog(req.requestURI), ex)
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "An unexpected error occurred")
    }

    private fun error(
        status: HttpStatus,
        code: String,
        message: String
    ) = ResponseEntity.status(status).body(
        ErrorResponse(
            code = code,
            message = message,
            timestamp = Instant.now(),
            correlationId = MDC.get("correlationId")
        )
    )
}