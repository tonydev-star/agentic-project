package com.example.agentic.controller

import com.example.agentic.model.CustomerFeedback
import com.example.agentic.model.SentimentScore
import com.example.agentic.repository.CustomerFeedbackRepository
import com.example.agentic.agent.LoggerAgent
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/v1/feedback")
@CrossOrigin(origins = ["http://localhost:3000"])
class FeedbackController(
    private val feedbackRepository: CustomerFeedbackRepository,
    private val loggerAgent: LoggerAgent
) {
    
    @GetMapping
    fun getAllFeedback(): List<CustomerFeedback> {
        return feedbackRepository.findAll()
    }
    
    @GetMapping("/{id}")
    fun getFeedbackById(@PathVariable id: Long): ResponseEntity<CustomerFeedback> {
        return feedbackRepository.findById(id)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }
    
    @GetMapping("/sentiment/{sentiment}")
    fun getFeedbackBySentiment(@PathVariable sentiment: SentimentScore): List<CustomerFeedback> {
        return feedbackRepository.findBySentiment(sentiment)
    }
    
    @GetMapping("/source/{source}")
    fun getFeedbackBySource(@PathVariable source: String): List<CustomerFeedback> {
        return feedbackRepository.findBySource(source)
    }
    
    @GetMapping("/unreviewed")
    fun getUnreviewedFeedback(): List<CustomerFeedback> {
        return feedbackRepository.findByHumanReviewedFalse()
    }
    
    @GetMapping("/recent")
    fun getRecentFeedback(@RequestParam(defaultValue = "24") hours: Int): List<CustomerFeedback> {
        val since = LocalDateTime.now().minusHours(hours.toLong())
        return feedbackRepository.findFeedbackSince(since)
    }
    
    @PostMapping("/{id}/human-review")
    fun markAsReviewed(
        @PathVariable id: Long,
        @RequestBody reviewRequest: HumanReviewRequest
    ): ResponseEntity<CustomerFeedback> {
        return feedbackRepository.findById(id).map { feedback ->
            feedback.humanReviewed = true
            feedback.humanResponse = reviewRequest.response
            
            val updatedFeedback = feedbackRepository.save(feedback)
            loggerAgent.logFeedbackEvent(updatedFeedback, "Human review completed")
            
            ResponseEntity.ok(updatedFeedback)
        }.orElse(ResponseEntity.notFound().build())
    }
    
    @PostMapping("/{id}/override-response")
    fun overrideResponse(
        @PathVariable id: Long,
        @RequestBody overrideRequest: OverrideRequest
    ): ResponseEntity<CustomerFeedback> {
        return feedbackRepository.findById(id).map { feedback ->
            feedback.autoResponse = overrideRequest.newResponse
            feedback.responseSentAt = LocalDateTime.now()
            
            val updatedFeedback = feedbackRepository.save(feedback)
            loggerAgent.logFeedbackEvent(updatedFeedback, "Response overridden")
            
            ResponseEntity.ok(updatedFeedback)
        }.orElse(ResponseEntity.notFound().build())
    }
    
    @GetMapping("/stats")
    fun getFeedbackStats(): FeedbackStats {
        val total = feedbackRepository.count()
        val positive = feedbackRepository.countBySentiment(SentimentScore.POSITIVE)
        val negative = feedbackRepository.countBySentiment(SentimentScore.NEGATIVE)
        val neutral = feedbackRepository.countBySentiment(SentimentScore.NEUTRAL)
        val unreviewed = feedbackRepository.findByHumanReviewedFalse().size.toLong()
        
        return FeedbackStats(
            total = total,
            positive = positive,
            negative = negative,
            neutral = neutral,
            unreviewed = unreviewed,
            positivePercentage = if (total > 0) (positive * 100.0 / total) else 0.0,
            negativePercentage = if (total > 0) (negative * 100.0 / total) else 0.0,
            neutralPercentage = if (total > 0) (neutral * 100.0 / total) else 0.0
        )
    }
}

data class HumanReviewRequest(
    val response: String
)

data class OverrideRequest(
    val newResponse: String
)

data class FeedbackStats(
    val total: Long,
    val positive: Long,
    val negative: Long,
    val neutral: Long,
    val unreviewed: Long,
    val positivePercentage: Double,
    val negativePercentage: Double,
    val neutralPercentage: Double
)
