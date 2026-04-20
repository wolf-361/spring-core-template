package com.template.core.features.item.infrastructure.seed

import com.template.core.features.item.application.repository.ItemRepository
import com.template.core.features.item.domain.model.Item
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@Profile("dev")
class ItemSeeder(
    private val itemRepository: ItemRepository
) : CommandLineRunner {
    private val log = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String) {
        if (itemRepository.findAllByUserId(DEV_USER_ID).isNotEmpty()) return

        itemRepository.save(
            Item(
                userId = DEV_USER_ID,
                name = "Sample Item",
                description = "A sample item seeded for development"
            )
        )
        log.info("Seeded sample item for dev user {}", DEV_USER_ID)
    }

    companion object {
        val DEV_USER_ID: UUID = UUID.fromString("00000000-0000-0000-0000-000000000001")
    }
}