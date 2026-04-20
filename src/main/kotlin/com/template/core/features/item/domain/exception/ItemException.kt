package com.template.core.features.item.domain.exception

import java.util.UUID

sealed class ItemException(
    message: String
) : RuntimeException(message) {
    class NotFound(
        id: UUID
    ) : ItemException("Item $id not found")
}