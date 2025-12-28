# Customer Feedback Automation Agent

## Overview

The **Customer Feedback Automation Agent** is a multi-agent system that **automatically collects, analyzes, responds to, and logs customer feedback**. This project demonstrates **workflow automation** with **multi-agent reasoning**, combining backend logic, frontend visualization, and optional AI-powered sentiment analysis.

---

## Features

* **Scraper Agent:** Collects feedback from multiple sources (Twitter, Yelp, Google Reviews — mock data for demo).
* **Analyzer Agent:** Performs sentiment analysis (Positive / Negative / Neutral).
* **Responder Agent:** Generates automated responses based on sentiment.
* **Logger Agent:** Monitors activity and logs metrics.
* **Real-Time Dashboard:** Displays feedback, sentiment breakdown, and responses.
* Optional AI integration for smarter sentiment analysis.

---

## Tech Stack

| Layer       | Technology                       |
| ----------- | -------------------------------- |
| Frontend    | React.js                         |
| Backend     | Kotlin + Spring Boot             |
| Database    | MySQL                            |
| Agents      | Kotlin classes / LangChain-ready |
| Automation  | Droid CLI compatible             |
| Optional AI | OpenAI API                       |

---

## Setup Instructions

### 1. MySQL Database

```bash
mysql -u root -p
CREATE DATABASE feedback_db;
USE feedback_db;

CREATE TABLE feedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    source VARCHAR(100),
    content TEXT,
    sentiment VARCHAR(20),
    response TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
EXIT;
```

### 2. Backend

```bash
cd backend/CustomerFeedbackBackend
chmod +x gradlew
./gradlew build
./gradlew bootRun
```

### 3. Frontend

```bash
cd frontend
npm install
npm start
```

### 4. Optional AI Integration

```bash
export OPENAI_API_KEY="your_openai_api_key"
```

---

## Usage

1. Open your browser at `http://localhost:3000`.
2. Submit feedback via the dashboard.
3. Agents automatically analyze sentiment, generate responses, and log data.
4. Monitor real-time charts and statistics.

---

## Key Benefits

* Automates customer feedback management
* Demonstrates **multi-agent reasoning**
* Provides real-time monitoring and analytics
* Extensible with AI or additional sources

---

## Optional Enhancements

* Connect to **live APIs** (Twitter, Yelp, Google Reviews)
* Integrate **LangChain agents** for smarter decision-making
* Add **Jenkins pipeline** for continuous automation

---

## Author

**Antony Wanjiru** – Developed as a demonstration of **agentic workflow automation and full-stack development**.

---


