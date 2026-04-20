package com.template.core.features.item.application.command

import java.util.UUID

data class GetItemCommand(
    val id: UUID,
    val userId: UUID
)