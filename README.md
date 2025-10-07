# AI Learning Companion

Welcome to **AI Learning Companion**, an intelligent platform designed to enhance and personalize your learning experience using AI-powered insights and interactive tools.

## Overview

AI Learning Companion leverages advanced artificial intelligence to guide learners through their educational journey. With adaptive support, interactive modules, and real-time feedback, this project aims to make learning more effective, engaging, and tailored to individual needs.

## Features

- **Personalized Learning Paths**: AI-driven recommendations optimize your study plan.
- **Interactive Modules**: Engaging content with quizzes, coding exercises, and multimedia.
- **Progress Tracking**: Visualize your progress and identify areas to improve.
- **Real-Time Feedback**: Get instant feedback on assignments and quizzes.
- **API Documentation**: Explore and test the backend API using Swagger UI.

## Project Structure

```
AI-Learning-Companion/
│
├── backend/    # Java Spring Boot backend
│   └── ...     # API, business logic, Swagger docs
│
├── frontend/   # React/TypeScript frontend
│   └── ...     # UI components, assets, styles
│
└── README.md
```

## Getting Started

### Prerequisites

- **Backend**: Java 11+ (recommended: Java 17), Maven or Gradle
- **Frontend**: Node.js & npm (or yarn)
- Optional: Docker (for containerized deployment)

### Installation

#### 1. Clone the repository

```bash
git clone https://github.com/AadeshThoppae/AI-Learning-Companion.git
cd AI-Learning-Companion
```

#### 2. Backend Setup (Java)

```bash
cd backend
# Build and run with Maven:
mvn spring-boot:run
# OR with Gradle:
./gradlew bootRun
```

- The backend API will be available at `http://localhost:8080`
- API documentation and testing via Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

#### 3. Frontend Setup (TypeScript/JavaScript/CSS)

```bash
cd frontend
npm install
npm start
```

- The frontend will be available at `http://localhost:3000`

#### 4. Access the Application

- Open your browser and navigate to [http://localhost:3000](http://localhost:3000) for the frontend.
- For API documentation, visit [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).

## Usage

1. Sign up or log in to your account.
2. Explore personalized learning modules.
3. Track your progress and receive AI-powered feedback.
4. Connect with peers and mentors for collaborative learning.

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

- **Author:** [Aadesh Thoppae](https://github.com/AadeshThoppae) and [Andreas Jack Christiansen](https://github.com/dressi123)
- **Repository:** [AI-Learning-Companion](https://github.com/AadeshThoppae/AI-Learning-Companion)

---

**Empower your learning journey with AI Learning Companion!**
