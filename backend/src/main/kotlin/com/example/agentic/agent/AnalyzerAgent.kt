package com.example.agentic.agent

import com.example.agentic.model.CustomerFeedback
import com.example.agentic.model.SentimentScore
import com.example.agentic.repository.CustomerFeedbackRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class AnalyzerAgent(
    private val feedbackRepository: CustomerFeedbackRepository
) {
    
    private val logger = LoggerFactory.getLogger(AnalyzerAgent::class.java)
    
    // Positive sentiment indicators
    private val positiveWords = setOf(
        "great", "excellent", "amazing", "wonderful", "fantastic", "awesome", "perfect",
        "love", "loved", "best", "good", "nice", "helpful", "friendly", "professional",
        "quick", "fast", "resolved", "solved", "recommend", "satisfied", "happy", "pleased",
        "outstanding", "superb", "brilliant", "exceptional", "marvelous", "terrific"
    )
    
    // Negative sentiment indicators
    private val negativeWords = setOf(
        "terrible", "awful", "horrible", "bad", "worst", "hate", "hated", "disappointed",
        "poor", "slow", "rude", "unhelpful", "unprofessional", "useless", "waste", "frustrated",
        "angry", "upset", "annoyed", "disgusted", "appalled", "shocked", "never", "again",
        "refuse", "cancel", "complaint", "issue", "problem", "error", "mistake", "wrong"
    )
    
    // Intensity modifiers
    private val intensifiers = setOf("very", "extremely", "really", "so", "too", "absolutely", "completely")
    
    @Scheduled(fixedRate = 60000) // Run every minute
    fun analyzeUnprocessedFeedback() {
        logger.info("Starting sentiment analysis...")
        
        val startTime = System.currentTimeMillis()
        var processedCount = 0
        var errorCount = 0
        
        try {
            val unprocessedFeedback = feedbackRepository.findByAutoResponseIsNull()
            
            unprocessedFeedback.forEach { feedback ->
                try {
                    val sentimentAnalysis = analyzeSentiment(feedback.content)
                    
                    feedback.sentiment = sentimentAnalysis.sentiment
                    feedback.sentimentConfidence = sentimentAnalysis.confidence
                    
                    feedbackRepository.save(feedback)
                    processedCount++
                    logger.info("Analyzed feedback from ${feedback.author}: ${sentimentAnalysis.sentiment} (${String.format("%.2f", sentimentAnalysis.confidence)})")
                    
                } catch (e: Exception) {
                    errorCount++
                    logger.error("Error analyzing feedback from ${feedback.author}: ${e.message}", e)
                }
            }
            
        } catch (e: Exception) {
            logger.error("Error during sentiment analysis: ${e.message}", e)
        } finally {
            val duration = System.currentTimeMillis() - startTime
            logger.info("Sentiment analysis completed: Processed $processedCount, Errors: $errorCount, Duration: ${duration}ms")
        }
    }
    
    private fun analyzeSentiment(text: String): SentimentAnalysis {
        val lowercaseText = text.lowercase()
        val words = lowercaseText.split(Regex("\\s+"))
        
        var positiveScore = 0.0
        var negativeScore = 0.0
        
        var i = 0
        while (i < words.size) {
            val word = words[i].replace(Regex("[^a-zA-Z]"), "")
            
            when {
                positiveWords.contains(word) -> {
                    val multiplier = if (i > 0 && intensifiers.contains(words[i-1])) 1.5 else 1.0
                    positiveScore += multiplier
                }
                negativeWords.contains(word) -> {
                    val multiplier = if (i > 0 && intensifiers.contains(words[i-1])) 1.5 else 1.0
                    negativeScore += multiplier
                }
            }
            i++
        }
        
        val totalScore = positiveScore + negativeScore
        val confidence = if (totalScore > 0) {
            (maxOf(positiveScore, negativeScore) / totalScore).coerceAtMost(1.0)
        } else {
            0.5 // Neutral confidence for no sentiment words
        }
        
        val sentiment = when {
            positiveScore > negativeScore -> SentimentScore.POSITIVE
            negativeScore > positiveScore -> SentimentScore.NEGATIVE
            else -> SentimentScore.NEUTRAL
        }
        
        return SentimentAnalysis(sentiment, confidence)
    }
    
    data class SentimentAnalysis(
        val sentiment: SentimentScore,
        val confidence: Double
    )
}
