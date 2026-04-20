package com.template.core.features.item.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "items")
class Item(
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    var id: UUID? = null,
    @Column(name = "user_id", nullable = false, updatable = false)
    var userId: UUID,
    @Column(nullable = false)
    var name: String,
    @Column
    var description: String? = null
) : AuditableEntity() {
    fun isOwnedBy(userId: UUID): Boolean = this.userId == userId
}