package com.template.core.core.clients.identity

import java.util.UUID

data class UserInfo(
    val id: UUID,
    val firstName: String,
    val lastName: String
)