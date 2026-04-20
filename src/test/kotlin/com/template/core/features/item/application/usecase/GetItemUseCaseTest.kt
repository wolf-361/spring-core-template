package com.template.core.features.item.application.usecase

import com.template.core.features.item.application.command.GetItemCommand
import com.template.core.features.item.application.repository.ItemRepository
import com.template.core.features.item.domain.exception.ItemException
import com.template.core.features.item.domain.model.Item
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Test
import java.util.UUID

class GetItemUseCaseTest {
    private val itemRepository = mockk<ItemRepository>()
    private val useCase = GetItemUseCase(itemRepository)

    @Test
    fun `execute returns result for owner`() {
        val userId = UUID.randomUUID()
        val item = Item(id = UUID.randomUUID(), userId = userId, name = "Test")
        every { itemRepository.findById(item.id!!) } returns item

        val result = useCase.execute(GetItemCommand(id = item.id!!, userId = userId))

        result.id shouldBe item.id
        result.name shouldBe "Test"
    }

    @Test
    fun `execute throws NotFound when item does not exist`() {
        val id = UUID.randomUUID()
        every { itemRepository.findById(id) } returns null

        shouldThrow<ItemException.NotFound> {
            useCase.execute(GetItemCommand(id = id, userId = UUID.randomUUID()))
        }
    }

    @Test
    fun `execute throws NotFound when requester is not the owner`() {
        val item = Item(id = UUID.randomUUID(), userId = UUID.randomUUID(), name = "Test")
        every { itemRepository.findById(item.id!!) } returns item

        shouldThrow<ItemException.NotFound> {
            useCase.execute(GetItemCommand(id = item.id!!, userId = UUID.randomUUID()))
        }
    }
}