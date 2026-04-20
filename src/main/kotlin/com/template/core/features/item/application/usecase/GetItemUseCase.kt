package com.template.core.features.item.application.usecase

import com.template.core.features.item.application.command.GetItemCommand
import com.template.core.features.item.application.repository.ItemRepository
import com.template.core.features.item.application.result.ItemResult
import com.template.core.features.item.domain.exception.ItemException
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class GetItemUseCase(
    private val itemRepository: ItemRepository
) {
    @Transactional(readOnly = true)
    fun execute(command: GetItemCommand): ItemResult {
        val item =
            itemRepository.findById(command.id)
                ?: throw ItemException.NotFound(command.id)
        if (!item.isOwnedBy(command.userId)) throw ItemException.NotFound(command.id)
        return item.toResult()
    }
}