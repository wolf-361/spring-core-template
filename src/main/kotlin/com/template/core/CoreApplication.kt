package com.template.core

import com.template.core.core.config.FlywayEnvironmentListener
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
class CoreApplication

fun main(args: Array<String>) {
    val application = SpringApplication(CoreApplication::class.java)
    application.addListeners(FlywayEnvironmentListener())
    application.run(*args)
}