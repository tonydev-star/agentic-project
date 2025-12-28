package com.example.agentic.agent

import com.example.agentic.model.CustomerFeedback
import com.example.agentic.model.SentimentScore
import com.example.agentic.repository.CustomerFeedbackRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ResponderAgent(
    private val feedbackRepository: CustomerFeedbackRepository
) {
    
    private val logger = LoggerFactory.getLogger(ResponderAgent::class.java)
    
    @Scheduled(fixedRate = 120000) // Run every 2 minutes
    fun generateResponses() {
        logger.info("Starting automated response generation...")
        
        val startTime = System.currentTimeMillis()
        var processedCount = 0
        var errorCount = 0
        
        try {
            // Get feedback that has been analyzed but doesn't have a response yet
            val feedbackNeedingResponse = feedbackRepository.findByAutoResponseIsNull()
                .filter { it.sentiment != SentimentScore.NEUTRAL }
            
            feedbackNeedingResponse.forEach { feedback ->
                try {
                    val response = generateResponse(feedback)
                    
                    feedback.autoResponse = response
                    feedback.responseSentAt = LocalDateTime.now()
                    
                    feedbackRepository.save(feedback)
                    processedCount++
                    logger.info("Generated automated response for ${feedback.author}")
                    
                } catch (e: Exception) {
                    errorCount++
                    logger.error("Error generating response for ${feedback.author}: ${e.message}", e)
                }
            }
            
        } catch (e: Exception) {
            logger.error("Error during response generation: ${e.message}", e)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logger.info("Response generation completed: Processed $processedCount, Errors: $errorCount, Duration: ${duration}ms")
        }
    }
    
    private fun generateResponse(feedback: CustomerFeedback): String {
        return when (feedback.sentiment) {
            SentimentScore.POSITIVE -> generatePositiveResponse(feedback)
            SentimentScore.NEGATIVE -> generateNegativeResponse(feedback)
            SentimentScore.NEUTRAL -> generateNeutralResponse(feedback)
        }
    }
    
    private fun generatePositiveResponse(feedback: CustomerFeedback): String {
        val responses = listOf(
            "Thank you so much for your kind words, ${feedback.author}! We're thrilled that you had a great experience with us. Your feedback means the world to our team!",
            "We're so happy to hear that you had a positive experience, ${feedback.author}! Thank you for taking the time to share your feedback. We look forward to serving you again!",
            "Wow, thank you for the wonderful feedback, ${feedback.author}! We're delighted that you enjoyed our service. Your satisfaction is our top priority!",
            "Your kind words truly made our day, ${feedback.author}! Thank you for choosing us and for sharing your positive experience. We appreciate your business!"
        )
        
        return responses.random()
    }
    
    private fun generateNegativeResponse(feedback: CustomerFeedback): String {
        val responses = listOf(
            "We're sincerely sorry to hear about your experience, ${feedback.author}. This is not the standard of service we aim to provide. We'd like to make this right - please contact our support team at support@example.com so we can address your concerns directly.",
            "Thank you for bringing this to our attention, ${feedback.author}. We apologize that we didn't meet your expectations. Your feedback is valuable and we're committed to improving. Please reach out to us so we can resolve this issue.",
            "We're very sorry that you had a negative experience, ${feedback.author}. This falls short of the service we strive to provide. We take your feedback seriously and would appreciate the opportunity to make things right.",
            "We apologize for the poor experience you had, ${feedback.author}. Your feedback helps us improve, and we'd like to address your concerns personally. Please contact our customer service team so we can work towards a solution."
        )
        
        return responses.random()
    }
    
    private fun generateNeutralResponse(feedback: CustomerFeedback): String {
        val responses = listOf(
            "Thank you for your feedback, ${feedback.author}. We appreciate you taking the time to share your thoughts with us. Is there anything specific we could do to improve your experience next time?",
            "We appreciate your feedback, ${feedback.author}. Thank you for sharing your perspective with us. We're always looking for ways to enhance our service.",
            "Thanks for providing your feedback, ${feedback.author}. We value all input from our customers as it helps us better understand their needs and expectations."
        )
        
        return responses.random()
    }
}
