package com.template.core.features.item.application.repository

import com.template.core.features.item.domain.model.Item
import java.util.UUID

interface ItemRepository {
    fun findById(id: UUID): Item?

    fun findAllByUserId(userId: UUID): List<Item>

    fun save(item: Item): Item

    fun deleteById(id: UUID)
}