package com.template.core.features.item.application.command

import java.util.UUID

data class CreateItemCommand(
    val userId: UUID,
    val name: String,
    val description: String?
)