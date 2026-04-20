package com.template.core.features.item.application.usecase

import com.template.core.features.item.application.command.CreateItemCommand
import com.template.core.features.item.application.repository.ItemRepository
import com.template.core.features.item.application.result.ItemResult
import com.template.core.features.item.domain.model.Item
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class CreateItemUseCase(
    private val itemRepository: ItemRepository
) {
    @Transactional
    fun execute(command: CreateItemCommand): ItemResult {
        val item =
            Item(
                userId = command.userId,
                name = command.name,
                description = command.description
            )
        val saved = itemRepository.save(item)
        return saved.toResult()
    }
}

internal fun Item.toResult() =
    ItemResult(
        id = id!!,
        userId = userId,
        name = name,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt
    )