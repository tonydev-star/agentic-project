# Quick Start Guide

## Prerequisites Setup

### 1. Java Installation
- Install Java 17 or higher
- Set JAVA_HOME environment variable
- Verify with: `java -version`

### 2. MySQL Setup
```sql
CREATE DATABASE feedback_db;
```

### 3. Node.js Installation
- Install Node.js 16+ from https://nodejs.org/

## Running the Project

### Backend (Windows Command Prompt)
```cmd
cd backend
gradlew bootRun
```

### Frontend (Windows Command Prompt)
```cmd
cd frontend
npm install
npm start
```

## Alternative: Using PowerShell

### Backend
```powershell
cd backend
.\gradlew bootRun
```

### Frontend
```powershell
cd frontend
npm install
npm start
```

## Access Points
- Backend API: http://localhost:8080/api/v1
- Frontend Dashboard: http://localhost:3000
- API Documentation: http://localhost:8080/api/v1/feedback

## Testing the System

1. Start both backend and frontend
2. Visit http://localhost:3000
3. The dashboard will show:
   - Mock feedback data (automatically generated)
   - Sentiment analysis results
   - Automated responses

## Optional AI Enhancement

Add your OpenAI API key to `backend/src/main/resources/application.properties`:
```properties
openai.api.key=your_openai_api_key_here
```

Restart the backend to enable AI-powered sentiment analysis and response generation.
