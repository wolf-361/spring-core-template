package com.template.core.core.exception

import com.template.core.features.item.domain.exception.ItemException
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BeanPropertyBindingResult
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import java.util.UUID

class GlobalExceptionHandlerTest {
    private val handler = GlobalExceptionHandler()
    private val req = mockk<HttpServletRequest>(relaxed = true)

    @BeforeEach
    fun setup() {
        every { req.requestURI } returns "/items/test"
    }

    @Test
    fun `handleItemNotFound returns 404 with ITEM_NOT_FOUND code`() {
        val response = handler.handleItemNotFound(ItemException.NotFound(UUID.randomUUID()), req)

        response.statusCode.value() shouldBe 404
        response.body?.code shouldBe "ITEM_NOT_FOUND"
        response.body?.message shouldBe "Item not found"
    }

    @Test
    fun `handleValidation returns 400 with field message`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "target")
        bindingResult.addError(FieldError("target", "name", "must not be blank"))
        val ex = mockk<MethodArgumentNotValidException>()
        every { ex.bindingResult } returns bindingResult

        val response = handler.handleValidation(ex)

        response.statusCode.value() shouldBe 400
        response.body?.code shouldBe "VALIDATION_ERROR"
        response.body?.message shouldBe "name: must not be blank"
    }

    @Test
    fun `handleValidation with no field errors returns generic message`() {
        val bindingResult = BeanPropertyBindingResult(Any(), "target")
        val ex = mockk<MethodArgumentNotValidException>()
        every { ex.bindingResult } returns bindingResult

        val response = handler.handleValidation(ex)

        response.statusCode.value() shouldBe 400
        response.body?.message shouldBe "Validation failed"
    }

    @Test
    fun `handleNotReadable returns 400 with BAD_REQUEST code`() {
        val ex = mockk<HttpMessageNotReadableException>(relaxed = true)

        val response = handler.handleNotReadable(ex)

        response.statusCode.value() shouldBe 400
        response.body?.code shouldBe "BAD_REQUEST"
    }

    @Test
    fun `handleIllegalArgument returns 400 with message`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("invalid uuid"))

        response.statusCode.value() shouldBe 400
        response.body?.code shouldBe "BAD_REQUEST"
        response.body?.message shouldBe "invalid uuid"
    }

    @Test
    fun `handleIllegalArgument with null message returns fallback`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException())

        response.statusCode shouldBe HttpStatus.BAD_REQUEST
        response.body?.message shouldBe "Bad request"
    }

    @Test
    fun `handleGeneric returns 500 with INTERNAL_ERROR code`() {
        val response = handler.handleGeneric(RuntimeException("boom"), req)

        response.statusCode.value() shouldBe 500
        response.body?.code shouldBe "INTERNAL_ERROR"
    }

    @Test
    fun `error response includes correlationId from MDC when present`() {
        val response = handler.handleIllegalArgument(IllegalArgumentException("test"))

        response.body?.correlationId shouldBe null
    }
}