# AI-Learning-Companion

# Project Title:
AI Learning Companion
# Team:
​​Aadeshvaaman Thoppae & Andreas Jack Christiansen
# Overview:
This project is an AI-powered learning platform website that helps students transform lecture notes or interview topics into summaries, flashcards, quizzes, and coding exercises, with automated feedback on correctness and coding best practices.
# Required Features:
Notes input 
User pastes notes/slides, notes sent to AI service
Summary/Flashcard generation
AI generation of notes, displayed to user
Quiz generation
AI generation of quiz questions, displayed to user
Coding question generation
AI generation of coding problems, displayed to user
Answer submission
User pastes coding answer, sent to AI service
Answer grading, feedback generation
AI grades answer and feedback displayed to user
# Use Cases:
Student Self Study Tutor part:
### Summary Generation:
As a student, I want to upload my lecture notes or slides so that I can receive a clear summary of the material, in order to better review and understand the key concepts.
### Quiz Generation:
As a student, I want the system to generate quizzes based on my lecture notes or slides so that I can test my comprehension of the concepts.
### Coding questions:
As a student, I want the system to generate 1–2 coding questions when the material is programming-related so that I can practice applying the concepts in code.
### Feedback:
As a student, I want to receive feedback on my answers (including correctness and best practices) so that I can identify mistakes and improve my learning.

# Interview Preb Tutor part:
### Interview Topic Summary:
As a student preparing for interviews, I want to upload or prompt an interview topic (for example Depth First Search) so that I can receive a clear summary of the topic to strengthen my understanding.
### Interview Quiz Creation:	
As a student preparing for interviews, I want the system to generate quiz questions about the selected topic so that I can test my comprehension.
### Interview Coding Problems:
As a student preparing for interviews, I want the system to generate coding problems related to the selected algorithm or concept so that I can practice applying it.
### Interview Feedback:
As a student preparing for interviews, I want to receive instant feedback on correctness and best practices for my solutions so that I can refine my problem-solving and coding style.
# Languages & Tools:
- Front-End: JavaScript/TypeScript with Next.js and TailwindCSS

- Backend:  Spring Boot (Java) that provides REST APIs and uses an in-memory store (Map/HashMap) for each session’s Q&A. LangChain4j library will be used to handle the AI calls (OpenAi, Claude, etc.)
# Progress Milestone, October 7:
By October 7 we expect to have implemented the core features of our project. 
Users will be able to upload lecture notes, which would then be sent to the AI service for processing. The system 
The system will be able to generate and display summaries, flashcards and basic quiz questions based on the notes.
A functional frontend interface that allows users to interact with these features.

We expect that by this time we will still need to implement the coding question generator, automated feedback system and the interview preb tutor features.

# Ideas if time permits: 
- User Authentication, persistent user data storage
- Adaptable Difficulty(harder quizzes/coding exercises)
- Scoring system, 
- Gamify(badges, streaks, ranks, levels)
- Code Editor within app
- AI Agent
- Custom tutor: 
- interactive learning -> conversations with students(hints, redirections)
- History saved -> identifies weaknesses and plans studying questions/guides
- RAG with CS textbook

# Tech Stack:
## Front-End: React (basic components, maybe Tailwind for speed).
## Backend:  Spring Boot(Java): (REST API, In-memory store (Map/HashMap) for each session’s Q&A)


# Development Phases:
### Setup
- Spring boot setup
- Basic front end setup
- FastAPI skeleton
### AI Integration
- FastAPI -> AI service
- Lecture notes -> summaries/flashcards, quiz questions
- Coding problems
- Grading + feedback logic
### Backend
- Java <-> Python API calls
- Clear and simple data structure for transfer 
- Error handling, retry logic
### Front end
- summary/flashcard UI, quiz questions 
- User answer -> feedback results
### Polish/Testing/clean up
- Styling, UX (tailwind)
- Code cleanup(efficiency/readability/comments)
- Final testing, ensure error handling, edge cases
