import { ApiResponse, FlashcardList, QuizList, Summary } from "@/types/documentTypes";

/** Base URL for the API endpoints */
export const API_BASE_URL = "http://localhost:8080";

/**
 * Retrieves or generates a summary of the uploaded document
 *
 * @returns Promise resolving to API response containing summary data
 * @throws Error if summary generation fails or server returns error status
 */
export const getSummary = async (): Promise<ApiResponse<Summary>> => {
	const res = await fetch(`${API_BASE_URL}/api/content/summary`, {
		method: "GET",
		credentials: "include",
	});

	const result: ApiResponse<Summary> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to generate summary");
	}

	return result;
};

/**
 * Retrieves or generates flashcards from the uploaded document
 *
 * @returns Promise resolving to API response containing flashcard list
 * @throws Error if flashcard generation fails or server returns error status
 */
export const getFlashcards = async (): Promise<ApiResponse<FlashcardList>> => {
	const res = await fetch(`${API_BASE_URL}/api/content/flashcards`, {
		method: "GET",
		credentials: "include",
	});

	const result: ApiResponse<FlashcardList> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to generate flashcards");
	}

	return result;
};

/**
 * Retrieves or generates quizzes from the uploaded document
 *
 * @returns Promise resolving to API response containing quiz list
 * @throws Error if quiz generation fails or server returns error status
 */
export const getQuizzes = async (): Promise<ApiResponse<QuizList>> => {
	const res = await fetch(`${API_BASE_URL}/api/content/quiz`, {
		method: "GET",
		credentials: "include",
	});

	const result: ApiResponse<QuizList> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to generate quizzes");
	}

	return result;
};
