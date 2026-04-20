package com.template.core.features.item.application.result

import java.time.Instant
import java.util.UUID

data class ItemResult(
    val id: UUID,
    val userId: UUID,
    val name: String,
    val description: String?,
    val createdAt: Instant,
    val updatedAt: Instant
)