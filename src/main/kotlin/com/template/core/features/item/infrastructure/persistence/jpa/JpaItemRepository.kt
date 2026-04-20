package com.template.core.features.item.infrastructure.persistence.jpa

import com.template.core.features.item.domain.model.Item
import org.springframework.data.jpa.repository.JpaRepository
import java.util.UUID

interface JpaItemRepository : JpaRepository<Item, UUID> {
    fun findAllByUserId(userId: UUID): List<Item>
}