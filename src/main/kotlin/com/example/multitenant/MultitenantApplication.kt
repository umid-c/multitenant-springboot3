package com.example.multitenant

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.transaction.annotation.EnableTransactionManagement


@SpringBootApplication(exclude = [LiquibaseAutoConfiguration::class])
@EnableTransactionManagement
@EnableScheduling
class MultitenantApplication

fun main(args: Array<String>) {
    runApplication<MultitenantApplication>(*args)
}
