package com.example.agentic.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "customer_feedback")
data class CustomerFeedback(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(nullable = false)
    val source: String, // e.g., "twitter", "yelp", "google_reviews"
    
    @Column(nullable = false, columnDefinition = "TEXT")
    val content: String,
    
    @Column(nullable = false)
    val author: String,
    
    @Column(name = "source_id", nullable = false)
    val sourceId: String, // ID from the original source
    
    @Column(name = "created_at_source")
    val createdAtSource: LocalDateTime,
    
    @Column(name = "scraped_at", nullable = false)
    val scrapedAt: LocalDateTime = LocalDateTime.now(),
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var sentiment: SentimentScore = SentimentScore.NEUTRAL,
    
    @Column(name = "sentiment_confidence")
    var sentimentConfidence: Double = 0.0,
    
    @Column(name = "auto_response", columnDefinition = "TEXT")
    var autoResponse: String? = null,
    
    @Column(name = "response_sent_at")
    var responseSentAt: LocalDateTime? = null,
    
    @Column(name = "human_reviewed")
    var humanReviewed: Boolean = false,
    
    @Column(name = "human_response", columnDefinition = "TEXT")
    var humanResponse: String? = null
)

enum class SentimentScore {
    POSITIVE, NEGATIVE, NEUTRAL
}
