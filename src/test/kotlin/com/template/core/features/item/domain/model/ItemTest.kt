package com.template.core.features.item.domain.model

import io.kotest.assertions.withClue
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import java.util.UUID

class ItemTest {
    @Test
    fun `isOwnedBy returns true when userId matches`() {
        val userId = UUID.randomUUID()
        val item = Item(userId = userId, name = "test")
        withClue("owner should match") { item.isOwnedBy(userId) shouldBe true }
    }

    @Test
    fun `isOwnedBy returns false for a different userId`() {
        val item = Item(userId = UUID.randomUUID(), name = "test")
        item.isOwnedBy(UUID.randomUUID()) shouldBe false
    }
}