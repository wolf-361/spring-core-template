package com.template.core.features.item.infrastructure.web.mapper

import com.template.core.features.item.application.command.CreateItemCommand
import com.template.core.features.item.application.result.ItemResult
import com.template.core.features.item.infrastructure.web.dto.request.CreateItemRequest
import com.template.core.features.item.infrastructure.web.dto.response.ItemResponse
import org.springframework.stereotype.Component
import java.util.UUID

@Component
class ItemWebMapper {
    fun toResponse(result: ItemResult): ItemResponse =
        ItemResponse(
            id = result.id,
            name = result.name,
            description = result.description,
            createdAt = result.createdAt,
            updatedAt = result.updatedAt
        )

    fun toCommand(
        request: CreateItemRequest,
        userId: UUID
    ): CreateItemCommand =
        CreateItemCommand(
            userId = userId,
            name = request.name,
            description = request.description
        )
}