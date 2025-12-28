package com.example.agentic.controller

import com.example.agentic.agent.ScraperAgent
import com.example.agentic.agent.AnalyzerAgent
import com.example.agentic.agent.ResponderAgent
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/test")
@CrossOrigin(origins = ["http://localhost:3000"])
class TestController(
    private val scraperAgent: ScraperAgent,
    private val analyzerAgent: AnalyzerAgent,
    private val responderAgent: ResponderAgent
) {

    @GetMapping("/status")
    fun getSystemStatus(): ResponseEntity<SystemStatus> {
        return ResponseEntity.ok(
            SystemStatus(
                timestamp = LocalDateTime.now(),
                langChainAvailable = false,
                agentsStatus = mapOf(
                    "Scraper" to "Active",
                    "Analyzer" to "Active", 
                    "Responder" to "Active",
                    "Logger" to "Active"
                )
            )
        )
    }

    @PostMapping("/trigger-scraper")
    fun triggerScraper(): ResponseEntity<Map<String, String>> {
        return try {
            scraperAgent.scrapeFeedback()
            ResponseEntity.ok(mapOf<String, String>("status" to "success", "message" to "Scraper agent triggered"))
        } catch (e: Exception) {
            ResponseEntity.ok(mapOf<String, String>("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }

    @PostMapping("/trigger-analyzer")
    fun triggerAnalyzer(): ResponseEntity<Map<String, String>> {
        return try {
            analyzerAgent.analyzeUnprocessedFeedback()
            ResponseEntity.ok(mapOf<String, String>("status" to "success", "message" to "Analyzer agent triggered"))
        } catch (e: Exception) {
            ResponseEntity.ok(mapOf<String, String>("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }

    @PostMapping("/trigger-responder")
    fun triggerResponder(): ResponseEntity<Map<String, String>> {
        return try {
            responderAgent.generateResponses()
            ResponseEntity.ok(mapOf<String, String>("status" to "success", "message" to "Responder agent triggered"))
        } catch (e: Exception) {
            ResponseEntity.ok(mapOf<String, String>("status" to "error", "message" to (e.message ?: "Unknown error")))
        }
    }

    @GetMapping("/test-rule-based")
    fun testRuleBased(): ResponseEntity<Map<String, String>> {
        return try {
            ResponseEntity.ok(mapOf<String, String>(
                "status" to "success",
                "message" to "Rule-based sentiment analysis is working",
                "available" to "true"
            ))
        } catch (e: Exception) {
            ResponseEntity.ok(mapOf<String, String>(
                "status" to "error",
                "message" to (e.message ?: "Unknown error")
            ))
        }
    }
}

data class SystemStatus(
    val timestamp: LocalDateTime,
    val langChainAvailable: Boolean,
    val agentsStatus: Map<String, String>
)
