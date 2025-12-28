package com.example.agentic.repository

import com.example.agentic.model.CustomerFeedback
import com.example.agentic.model.SentimentScore
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface CustomerFeedbackRepository : JpaRepository<CustomerFeedback, Long> {
    
    fun findBySource(source: String): List<CustomerFeedback>
    
    fun findBySentiment(sentiment: SentimentScore): List<CustomerFeedback>
    
    fun findByHumanReviewedFalse(): List<CustomerFeedback>
    
    fun findByAutoResponseIsNull(): List<CustomerFeedback>
    
    @Query("SELECT f FROM CustomerFeedback f WHERE f.scrapedAt >= :since")
    fun findFeedbackSince(@Param("since") since: LocalDateTime): List<CustomerFeedback>
    
    @Query("SELECT f FROM CustomerFeedback f WHERE f.sentiment = :sentiment AND f.humanReviewed = false")
    fun findUnreviewedBySentiment(@Param("sentiment") sentiment: SentimentScore): List<CustomerFeedback>
    
    @Query("SELECT COUNT(f) FROM CustomerFeedback f WHERE f.sentiment = :sentiment")
    fun countBySentiment(@Param("sentiment") sentiment: SentimentScore): Long
    
    fun existsBySourceIdAndSource(sourceId: String, source: String): Boolean
}
