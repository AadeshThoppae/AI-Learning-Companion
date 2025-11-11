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

## AI Tools Usage
### Tools Used
* ChatGPT & Gemini - for code explanations, debugging help and drafting JavaDoc and JSDoc.
* Github Copilot - for inline code suggestions and autocomplete on the frontend. Copilot was also used to generate draft summaries for pull requests and to request minor code review suggestions directly in GitHub.
* Claude - Inline CSS suggestions for streamlined UI/UX

#### Representable Prompts and Reflections
##### Prompt 1:
> "Currently this flips from bottom-to-top and then from top-to-bottom can i make it flip always from bottom-to-top so it keeps flipping all the way around?"
##### Reflection:
We provided our existing code snippet, and Copilot generated a solid solution for making the flashcard always flip from bottom to top. It also explained its reasoning and the expected behavior. However, it introduced an unnecessary variable called `isFlipped`. We implemented Copilot’s suggestion with a few manual tweaks to simplify the logic and make the animation work as intended.

##### Prompt 2:
> "Can you help me generate simple JavaDoc for this code ..."
##### Reflection:
ChatGPT is great at writing, so it naturally produced detailed and well-structured JavaDoc comments. However, the generated documentation was often too long and more detailed than necessary. We used its output as a solid draft and then refined it to match our project’s style and level of detail.

##### Prompt 3:
> "I need help organizing my project structure to follow SOLID principles. How should I split this large component file into smaller, more maintainable modules?"
##### Reflection:
AI tools (ChatGPT and Claude) were instrumental in helping us refactor and organize our codebase to follow SOLID principles. The AI provided clear suggestions on how to break down large files into smaller, focused modules with single responsibilities. While the overall architectural guidance was excellent, we had to manually adjust some of the suggested file structures to better match our existing project conventions and ensure proper separation of concerns. The AI gave us a good starting point, but we refined the implementation based on our specific use cases.

##### Prompt 4:
> "I'm getting an error when executing code in my code executor component. The code runs but the output isn't displaying correctly. Here's the error and my current implementation..."
##### Reflection:
Debugging the code executor was one of the most challenging parts of the project, and AI tools were extensively used to troubleshoot issues. We used ChatGPT and Claude to help identify problems with state management, output capture, and asynchronous execution handling. The AI was particularly helpful in suggesting different approaches to capture console output and handle errors. However, many of the initial suggestions needed significant debugging and manual adjustments because the AI couldn't fully understand the context of our Monaco Editor integration and the specific execution environment. We iterated multiple times, using AI feedback as a guide while manually testing and refining the solution.

##### GitHub Copilot for PR Drafts and Reviews

##### Reflection:
We used GitHub Copilot's built-in GitHub integration to assist with pull requests across multiple PRs. For example, in PR [#14](https://github.com/AadeshThoppae/AI-Learning-Companion/pull/14), we clicked "Write draft summary" to generate a clear pull request description and "Request code review" to let Copilot highlight minor typos and small code improvements before the human review. This feature helped streamline the review process, reduce trivial corrections, and still allowed us to verify and adjust all changes manually.

## Tutorials and External References
- Card Flip Animation Tutorial: [A Card Flip with Tailwind](https://www.telerik.com/blogs/card-flip-tailwind)
    - Used this tutorial to learn the basics of creating a flip animation using Tailwind CSS. The tutorial helped me understand how to use perspective, rotation, and transform utilities to create smooth transitions.

- Next.js Documentation: [Nextjs](https://nextjs.org/docs)
  - Used for understanding the app structure, routing, and built-in methods.

- Tailwind CSS Documentation: [Tailwindcss](https://tailwindcss.com/docs)
  - Used extensively for styling components and building responsive layouts.

- React Split Documentation: [React Split](https://uiwjs.github.io/react-split/)
  - Used extensively for implementing split pane layouts in the code editor interface. The documentation provided examples for creating resizable panels and handling split configurations.

- Monaco Editor Documentation: [Monaco Editor](https://microsoft.github.io/monaco-editor/)
  - Used extensively for integrating the Monaco Editor (VS Code's editor) into the code execution environment. The documentation was essential for understanding editor configuration, syntax highlighting, and API integration.

## License

This project is licensed under the [MIT License](LICENSE).

## Contact

- **Author:** [Aadesh Thoppae](https://github.com/AadeshThoppae) and [Andreas Jack Christiansen](https://github.com/dressi123)
- **Repository:** [AI-Learning-Companion](https://github.com/AadeshThoppae/AI-Learning-Companion)

---

**Empower your learning journey with AI Learning Companion!**
