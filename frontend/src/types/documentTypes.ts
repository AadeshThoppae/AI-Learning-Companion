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
 * Container for a collection of flashcards
 */
export interface FlashcardList {
	flashcards: Flashcard[];
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
