/**
 * Represents a document summary with title and key points
 */
export interface Summary {
	title: string;
	keyPoints: string[];
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

export interface Quiz {
	id: number;
	question: string;
	options: QuizOption[];
	answerId: number;
}

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
