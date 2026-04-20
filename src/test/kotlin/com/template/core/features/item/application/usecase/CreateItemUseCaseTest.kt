package com.template.core.features.item.application.usecase

import com.template.core.features.item.application.command.CreateItemCommand
import com.template.core.features.item.application.repository.ItemRepository
import com.template.core.features.item.domain.model.Item
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.junit.jupiter.api.Test
import java.util.UUID

class CreateItemUseCaseTest {
    private val itemRepository = mockk<ItemRepository>()
    private val useCase = CreateItemUseCase(itemRepository)

    @Test
    fun `execute saves item and returns result with correct fields`() {
        val userId = UUID.randomUUID()
        val command = CreateItemCommand(userId = userId, name = "My Item", description = "Details")
        val slot = slot<Item>()
        every { itemRepository.save(capture(slot)) } answers { slot.captured.apply { id = UUID.randomUUID() } }

        val result = useCase.execute(command)

        result.userId shouldBe userId
        result.name shouldBe "My Item"
        result.description shouldBe "Details"
    }

    @Test
    fun `execute saves item with null description`() {
        val userId = UUID.randomUUID()
        val command = CreateItemCommand(userId = userId, name = "Minimal", description = null)
        val slot = slot<Item>()
        every { itemRepository.save(capture(slot)) } answers { slot.captured.apply { id = UUID.randomUUID() } }

        val result = useCase.execute(command)

        result.description shouldBe null
    }
}