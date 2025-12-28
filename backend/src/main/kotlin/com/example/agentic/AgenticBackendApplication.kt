package com.example.agentic

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class AgenticBackendApplication

fun main(args: Array<String>) {
	runApplication<AgenticBackendApplication>(*args)
}
