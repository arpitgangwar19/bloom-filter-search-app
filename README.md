# bloom-feed-seach-app
# 📰 Feed Search App

A Spring Boot + MongoDB-based microservice for managing and searching multimedia-rich feed items. This application supports uploading images (stored in GridFS or locally), full-text search by feed title, and generating test feeds for development purposes.

Both the API and MongoDB run in Docker containers using Docker Compose.

---

## 📦 Features

- Create and retrieve feed items with metadata (title, description, tags)
- Full-text search by title
- Upload and store images using:
    - MongoDB GridFS
    - Local file system
- Retrieve feed images by ID
- Generate bulk test feeds

---

## 🧰 Tech Stack

- **Java 17** + **Spring Boot**
- **MongoDB** (with GridFS for image storage)
- **Lombok**
- **Docker** + **Docker Compose**

---

## Commands to Run
docker-compose up --build

## 🚀 Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/feed-search-app.git](https://github.com/arpitgangwar19/bloom-feed-seach-app.git
cd feed-search-app

