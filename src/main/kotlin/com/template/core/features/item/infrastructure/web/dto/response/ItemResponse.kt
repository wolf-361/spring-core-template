package com.template.core.features.item.infrastructure.web.dto.response

import java.time.Instant
import java.util.UUID

data class ItemResponse(
    val id: UUID,
    val name: String,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)