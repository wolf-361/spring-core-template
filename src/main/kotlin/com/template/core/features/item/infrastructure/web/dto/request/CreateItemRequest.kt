package com.template.core.features.item.infrastructure.web.dto.request

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class CreateItemRequest(
    @field:NotBlank
    @field:Size(max = 255)
    val name: String,
    val description: String? = null
)