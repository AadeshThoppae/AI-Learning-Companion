export interface Summary {
	title: string;
	keyPoints: string[];
}

export interface Flashcard {
	id: number;
	question: string;
	answer: string;
	hint: string;
}

export interface FlashcardList {
	flashcards: Flashcard[];
}

export interface ApiResponse<T> {
	message: string;
	code: string;
	data: T | null;
}
