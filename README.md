# Customer Feedback Automation Agent

A multi-agent system for automatically collecting, analyzing, and responding to customer feedback across multiple platforms.

## Architecture

### Backend (Kotlin + Spring Boot)
- **Scraper Agent**: Collects reviews from Twitter, Yelp, and Google Reviews
- **Analyzer Agent**: Performs sentiment analysis using both rule-based and AI-powered approaches
- **Responder Agent**: Generates automated responses to customer feedback
- **Logger Agent**: Monitors system metrics and generates daily reports

### Frontend (React + Tailwind CSS)
- Real-time dashboard showing feedback statistics
- Sentiment analysis visualization
- Feedback review and response management
- Interactive charts and metrics

### Technology Stack
- **Backend**: Kotlin, Spring Boot, JPA, MySQL
- **Frontend**: React, Tailwind CSS, Recharts, Axios
- **AI Integration**: LangChain4j with OpenAI GPT (optional)
- **Database**: MySQL

## Features

- **Multi-source Scraping**: Automatically collects feedback from Twitter, Yelp, and Google Reviews
- **Intelligent Sentiment Analysis**: Rule-based analysis with optional AI enhancement via LangChain
- **Automated Response Generation**: Context-aware responses for positive and negative feedback
- **Real-time Dashboard**: Live monitoring of feedback metrics and sentiment trends
- **Human Review System**: Manual review and override capabilities for quality control
- **Comprehensive Logging**: System metrics, performance monitoring, and daily reports

## Setup Instructions

### Prerequisites
- Java 17+
- Node.js 16+
- MySQL 8.0+
- (Optional) OpenAI API key for enhanced AI features

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE feedback_db;
```

2. Update database credentials in `backend/src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/feedback_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### Backend Setup

1. Navigate to backend directory:
```bash
cd backend
```

2. Build and run the application:
```bash
./gradlew bootRun
```

The backend will start on `http://localhost:8080`

### Frontend Setup

1. Navigate to frontend directory:
```bash
cd frontend
```

2. Install dependencies:
```bash
npm install
```

3. Start the development server:
```bash
npm start
```

The frontend will start on `http://localhost:3000`

### Optional: OpenAI Integration

For enhanced sentiment analysis and response generation:

1. Get an OpenAI API key from https://platform.openai.com/
2. Add it to `backend/src/main/resources/application.properties`:
```properties
openai.api.key=your_openai_api_key
openai.model.name=gpt-3.5-turbo
```

If no API key is provided, the system will automatically fall back to rule-based analysis.

## API Endpoints

### Feedback Management
- `GET /api/v1/feedback` - Get all feedback
- `GET /api/v1/feedback/{id}` - Get specific feedback
- `GET /api/v1/feedback/sentiment/{sentiment}` - Filter by sentiment
- `GET /api/v1/feedback/source/{source}` - Filter by source
- `GET /api/v1/feedback/unreviewed` - Get unreviewed feedback
- `GET /api/v1/feedback/recent?hours=24` - Get recent feedback
- `POST /api/v1/feedback/{id}/human-review` - Mark as reviewed
- `POST /api/v1/feedback/{id}/override-response` - Override automated response
- `GET /api/v1/feedback/stats` - Get feedback statistics

## Agent Scheduling

The agents run automatically on the following schedules:
- **Scraper Agent**: Every 5 minutes
- **Analyzer Agent**: Every 1 minute
- **Responder Agent**: Every 2 minutes
- **Logger Agent**: Every 5 minutes (metrics), Daily at midnight (reports)

## Monitoring and Logging

The system provides comprehensive logging:
- Agent performance metrics
- Processing statistics
- Error tracking
- Daily summary reports

## Development Notes

### Mock Data
The scraper agents currently use mock data for demonstration purposes. In production, you would integrate with:
- Twitter API for tweet scraping
- Yelp API for review collection
- Google Places API for Google Reviews

### Extending the System
- Add new sources by implementing additional scraper methods
- Customize sentiment analysis rules in `AnalyzerAgent`
- Modify response templates in `ResponderAgent`
- Add new metrics and reports in `LoggerAgent`

## Security Considerations

- API keys should be stored securely (environment variables in production)
- Database credentials should use proper security practices
- CORS is configured for development - adjust for production
- Input validation should be added for user-facing endpoints

## Performance

- Database queries are optimized with proper indexing
- Scheduled tasks run at appropriate intervals to avoid resource conflicts
- Error handling prevents cascading failures
- Metrics tracking helps identify performance bottlenecks

## License

This project is for educational and demonstration purposes.
