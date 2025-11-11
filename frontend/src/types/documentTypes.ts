/**
 * Represents a document summary with title and key points
 */
export interface Summary {
	title: string;
	keyPoints: string[];
}

/**
 * Represents a single interview question
 */
export interface InterviewQuestion{
    id:number;
    question: string;
    answer: string;
}

/**
 * Container for collection of interview questions
 */
export interface Interview{
    questions: InterviewQuestion[];
}
/**
 * Request payload for grading an interview answer
 */
export interface InterviewGradingRequest{
    questionId: number;
    userAnswer:string;
}

/**
 * Response containing AI grading feedback
 */
export interface InterviewResponse{
    userAnswer: string;
    score: number;
    feedback: string;
    suggestions: string;
    strengths: string;
}
/**
 * Represents a single flashcard for studying
 */
export interface Flashcard {
	id: number;
	question: string;
	answer: string;
	hint: string;
}

/**
 * Container for a collection of flashcards in the format returned by the API
 */
export interface FlashcardList {
	flashcards: Flashcard[];
}

/**
 * Represents a single quiz question with multiple choice options
 */
export interface Quiz {
	id: number;
	question: string;
	options: QuizOption[];
	answerId: number;
}

/**
 * Represents a single answer option for a quiz question
 */
export interface QuizOption {
	id: number;
	option: string;
	explanation: string;
}

/**
 * Container for a collection of Quizzes in the format returned by the API
 */
export interface QuizList {
	quiz: Quiz[];
}

/**
 * Generic API response wrapper for all backend communications
 *
 * @template T - The type of data contained in the response
 */
export interface ApiResponse<T> {
	message: string;
	code: string;
	data: T | null;
}
