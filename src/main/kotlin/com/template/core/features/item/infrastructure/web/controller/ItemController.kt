package com.template.core.features.item.infrastructure.web.controller

import com.template.core.features.item.application.command.GetItemCommand
import com.template.core.features.item.application.usecase.CreateItemUseCase
import com.template.core.features.item.application.usecase.GetItemUseCase
import com.template.core.features.item.infrastructure.web.dto.request.CreateItemRequest
import com.template.core.features.item.infrastructure.web.dto.response.ItemResponse
import com.template.core.features.item.infrastructure.web.mapper.ItemWebMapper
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
@RequestMapping("/items")
class ItemController(
    private val createItemUseCase: CreateItemUseCase,
    private val getItemUseCase: GetItemUseCase,
    private val mapper: ItemWebMapper
) {
    @PostMapping
    fun create(
        @Valid @RequestBody request: CreateItemRequest,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ItemResponse> {
        val result = createItemUseCase.execute(mapper.toCommand(request, userId))
        return ResponseEntity.status(201).body(mapper.toResponse(result))
    }

    @GetMapping("/{id}")
    fun getById(
        @PathVariable id: UUID,
        @AuthenticationPrincipal userId: UUID
    ): ResponseEntity<ItemResponse> {
        val result = getItemUseCase.execute(GetItemCommand(id = id, userId = userId))
        return ResponseEntity.ok(mapper.toResponse(result))
    }
}