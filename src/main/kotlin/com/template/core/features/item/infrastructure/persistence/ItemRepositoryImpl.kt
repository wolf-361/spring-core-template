package com.template.core.features.item.infrastructure.persistence

import com.template.core.features.item.application.repository.ItemRepository
import com.template.core.features.item.domain.model.Item
import com.template.core.features.item.infrastructure.persistence.jpa.JpaItemRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
class ItemRepositoryImpl(
    private val jpa: JpaItemRepository
) : ItemRepository {
    override fun findById(id: UUID): Item? = jpa.findById(id).orElse(null)

    override fun findAllByUserId(userId: UUID): List<Item> = jpa.findAllByUserId(userId)

    override fun save(item: Item): Item = jpa.save(item)

    override fun deleteById(id: UUID) = jpa.deleteById(id)
}