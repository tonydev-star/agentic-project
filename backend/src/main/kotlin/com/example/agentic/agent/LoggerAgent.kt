package com.example.agentic.agent

import com.example.agentic.model.CustomerFeedback
import com.example.agentic.model.SentimentScore
import com.example.agentic.repository.CustomerFeedbackRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Service
class LoggerAgent(
    private val feedbackRepository: CustomerFeedbackRepository
) {
    
    private val logger = LoggerFactory.getLogger(LoggerAgent::class.java)
    
    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    fun logSystemMetrics() {
        try {
            val metrics = collectSystemMetrics()
            logMetrics(metrics)
        } catch (e: Exception) {
            logger.error("Error logging system metrics: ${e.message}", e)
        }
    }
    
    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    fun generateDailyReport() {
        try {
            val report = generateDailySummary()
            logger.info("=== DAILY FEEDBACK REPORT ===")
            logger.info(report)
            logger.info("=== END DAILY REPORT ===")
        } catch (e: Exception) {
            logger.error("Error generating daily report: ${e.message}", e)
        }
    }
    
    private fun collectSystemMetrics(): SystemMetrics {
        val totalFeedback = feedbackRepository.count()
        val positiveCount = feedbackRepository.countBySentiment(SentimentScore.POSITIVE)
        val negativeCount = feedbackRepository.countBySentiment(SentimentScore.NEGATIVE)
        val neutralCount = feedbackRepository.countBySentiment(SentimentScore.NEUTRAL)
        
        val today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0)
        val todayFeedback = feedbackRepository.findFeedbackSince(today)
        
        val unprocessedCount = feedbackRepository.findByAutoResponseIsNull().size
        val unreviewedCount = feedbackRepository.findByHumanReviewedFalse().size
        
        return SystemMetrics(
            totalFeedback = totalFeedback,
            positiveCount = positiveCount,
            negativeCount = negativeCount,
            neutralCount = neutralCount,
            todayCount = todayFeedback.size.toLong(),
            unprocessedCount = unprocessedCount.toLong(),
            unreviewedCount = unreviewedCount.toLong(),
            timestamp = LocalDateTime.now()
        )
    }
    
    private fun logMetrics(metrics: SystemMetrics) {
        logger.info("System Metrics - Total: ${metrics.totalFeedback}, " +
                   "Positive: ${metrics.positiveCount}, " +
                   "Negative: ${metrics.negativeCount}, " +
                   "Neutral: ${metrics.neutralCount}, " +
                   "Today: ${metrics.todayCount}, " +
                   "Unprocessed: ${metrics.unprocessedCount}, " +
                   "Unreviewed: ${metrics.unreviewedCount}")
    }
    
    private fun generateDailySummary(): String {
        val today = LocalDateTime.now().minusDays(1).withHour(0).withMinute(0).withSecond(0)
        val yesterday = today.plusDays(1)
        
        val yesterdayFeedback = feedbackRepository.findFeedbackSince(today)
            .filter { it.scrapedAt.isBefore(yesterday) }
        
        val positiveCount = yesterdayFeedback.count { it.sentiment == SentimentScore.POSITIVE }
        val negativeCount = yesterdayFeedback.count { it.sentiment == SentimentScore.NEGATIVE }
        val neutralCount = yesterdayFeedback.count { it.sentiment == SentimentScore.NEUTRAL }
        
        val sources = yesterdayFeedback.groupBy { it.source }
            .mapValues { it.value.size }
        
        val report = StringBuilder()
        report.appendLine("Date: ${today.format(DateTimeFormatter.ISO_LOCAL_DATE)}")
        report.appendLine("Total Feedback Processed: ${yesterdayFeedback.size}")
        report.appendLine("Sentiment Breakdown:")
        report.appendLine("  - Positive: $positiveCount (${if (yesterdayFeedback.isNotEmpty()) positiveCount * 100 / yesterdayFeedback.size else 0}%)")
        report.appendLine("  - Negative: $negativeCount (${if (yesterdayFeedback.isNotEmpty()) negativeCount * 100 / yesterdayFeedback.size else 0}%)")
        report.appendLine("  - Neutral: $neutralCount (${if (yesterdayFeedback.isNotEmpty()) neutralCount * 100 / yesterdayFeedback.size else 0}%)")
        report.appendLine("Sources:")
        sources.forEach { (source, count) ->
            report.appendLine("  - $source: $count")
        }
        
        if (yesterdayFeedback.isNotEmpty()) {
            val avgConfidence = yesterdayFeedback.map { it.sentimentConfidence }.average()
            report.appendLine("Average Sentiment Confidence: ${String.format("%.2f", avgConfidence)}")
        }
        
        return report.toString()
    }
    
    fun logFeedbackEvent(feedback: CustomerFeedback, event: String) {
        logger.info("Feedback Event - ID: ${feedback.id}, Author: ${feedback.author}, " +
                   "Source: ${feedback.source}, Event: $event, " +
                   "Sentiment: ${feedback.sentiment}, Confidence: ${String.format("%.2f", feedback.sentimentConfidence)}")
    }
    
    fun logAgentPerformance(agentName: String, processedCount: Int, errorCount: Int, durationMs: Long) {
        logger.info("Agent Performance - Agent: $agentName, Processed: $processedCount, " +
                   "Errors: $errorCount, Duration: ${durationMs}ms")
    }
    
    data class SystemMetrics(
        val totalFeedback: Long,
        val positiveCount: Long,
        val negativeCount: Long,
        val neutralCount: Long,
        val todayCount: Long,
        val unprocessedCount: Long,
        val unreviewedCount: Long,
        val timestamp: LocalDateTime
    )
}
