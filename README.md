📰 Fake News Detection System

A full-stack web application that analyzes news content and classifies it as FAKE or REAL using simple keyword-based logic. The system stores results in a MySQL database and displays past analyses in a dashboard.

🚀 Features
🔍 Analyze news articles (title + content)
⚡ Instant classification (FAKE / REAL)
💾 Store results in MySQL database
📊 Dashboard to view history
🌐 REST API using Java Servlets
🎯 Simple and clean frontend UI
🏗️ Tech Stack
Backend
Java (JDK 21)
Jakarta Servlet (v6.0)
MySQL Database
JDBC (MySQL Connector)
Frontend
HTML5
CSS3
JavaScript (Vanilla)
Build Tool
Maven
📂 Project Structure
fakenewsdetector/
│── src/main/java/com/fakenews/
│   ├── DatabaseConnection.java
│   ├── NewsDAO.java
│   ├── NewsServlet.java
│
│── src/main/webapp/
│   ├── index.html
│   ├── form.html
│   ├── result.html
│   ├── dashboard.html
│   ├── script.js
│   ├── style.css
│
│── pom.xml
│── web.xml
⚙️ Setup Instructions
1️⃣ Clone the Repository
git clone https://github.com/your-username/fakenewsdetector.git
cd fakenewsdetector
2️⃣ Setup MySQL Database

Open MySQL and run:

CREATE DATABASE fakenews_db;

USE fakenews_db;

CREATE TABLE news (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(255),
    content TEXT,
    source VARCHAR(100),
    result VARCHAR(10),
    checked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
3️⃣ Configure Database Connection

Edit:

DatabaseConnection.java

Update your MySQL password:

private static final String PASSWORD = "your_password";
4️⃣ Build the Project
mvn clean install

This will generate a .war file.

5️⃣ Deploy on Server

Deploy the WAR file on:

Apache Tomcat (recommended)

Copy WAR file to:

apache-tomcat/webapps/

Start server:

startup.bat
6️⃣ Run Application

Open browser:

http://localhost:8080/fakenewsdetector-1.0/
🔌 API Endpoints
➤ POST /api/news

Analyze and store news

Request Body:

{
  "title": "Sample Title",
  "content": "Some news content",
  "source": "Unknown"
}

Response:

{
  "result": "FAKE",
  "title": "Sample Title",
  "source": "Unknown",
  "message": "News checked and saved successfully!"
}
➤ GET /api/news

Fetch all analyzed news

Response:

[
  {
    "title": "News Title",
    "source": "Unknown",
    "result": "REAL",
    "date": "2026-05-05 10:00:00"
  }
]
🧠 Detection Logic

Currently uses simple keyword matching:

FAKE if content contains:

fake
hoax
false
misleading
fabricated

Otherwise → REAL

⚠️ Known Limitations
❌ Not AI-based (only keyword logic)
❌ No authentication system
❌ Basic UI design
❌ No real fact-checking APIs
🔮 Future Enhancements
🤖 Integrate Machine Learning / NLP
🌍 Real-time fact-check APIs
🔐 User authentication (Login/Register)
📈 Advanced analytics dashboard
🖼️ Image verification support
