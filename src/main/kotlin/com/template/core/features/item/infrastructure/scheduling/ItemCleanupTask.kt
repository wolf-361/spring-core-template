package com.template.core.features.item.infrastructure.scheduling

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class ItemCleanupTask {
    private val log = LoggerFactory.getLogger(javaClass)

    @Scheduled(cron = "0 0 * * * *")
    fun cleanExpiredItems() {
        log.info("Item cleanup task ran")
    }
}