package com.template.core.features.item.infrastructure.web.mapper

import com.template.core.features.item.application.result.ItemResult
import com.template.core.features.item.infrastructure.web.dto.request.CreateItemRequest
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.time.Instant
import java.util.UUID

class ItemWebMapperTest {
    private val mapper = ItemWebMapper()

    @Test
    fun `toResponse maps all fields from ItemResult`() {
        val result =
            ItemResult(
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                name = "Test Item",
                description = "A description",
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

        val response = mapper.toResponse(result)

        response.id shouldBe result.id
        response.name shouldBe result.name
        response.description shouldBe result.description
        response.createdAt shouldBe result.createdAt
        response.updatedAt shouldBe result.updatedAt
    }

    @Test
    fun `toResponse maps null description`() {
        val result =
            ItemResult(
                id = UUID.randomUUID(),
                userId = UUID.randomUUID(),
                name = "No Description",
                description = null,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

        mapper.toResponse(result).description shouldBe null
    }

    @Test
    fun `toCommand maps CreateItemRequest with userId`() {
        val userId = UUID.randomUUID()
        val request = CreateItemRequest(name = "My Item", description = "Details")

        val command = mapper.toCommand(request, userId)

        command.userId shouldBe userId
        command.name shouldBe "My Item"
        command.description shouldBe "Details"
    }

    @Test
    fun `toCommand preserves null description`() {
        val userId = UUID.randomUUID()
        val request = CreateItemRequest(name = "Minimal")

        mapper.toCommand(request, userId).description shouldBe null
    }
}