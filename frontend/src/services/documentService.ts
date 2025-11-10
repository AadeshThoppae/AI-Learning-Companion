import {ApiResponse, FlashcardList, Interview, InterviewResponse, QuizList, Summary} from "@/types/documentTypes";

/** Base URL for the API endpoints */
const API_BASE_URL = "http://localhost:8080";

/**
 * Uploads text content to the server for processing
 *
 * @param text - The text content to upload and process
 * @returns Promise resolving to API response with null data on success
 * @throws Error if upload fails or server returns error status
 */
export const uploadText = async (text: string): Promise<ApiResponse<null>> => {
	const res = await fetch(`${API_BASE_URL}/api/documents/upload-text`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ text }),
		credentials: "include",
	});

	const result: ApiResponse<null> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to upload text");
	}

	return result;
};

/**
 * Uploads a PDF file to the server for processing
 *
 * @param file - The PDF file to upload
 * @returns Promise resolving to API response with null data on success
 * @throws Error if upload fails or server returns error status
 */
export const uploadPDF = async (file: File): Promise<ApiResponse<null>> => {
	const formData = new FormData();
	formData.append("file", file);
	const res = await fetch(`${API_BASE_URL}/api/documents/upload`, {
		method: "POST",
		body: formData,
		credentials: "include",
	});

	const result: ApiResponse<null> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to upload pdf");
	}

	return result;
};

/**
 * Retrieves or generates a summary of the uploaded document
 *
 * @returns Promise resolving to API response containing summary data
 * @throws Error if summary generation fails or server returns error status
 */
export const getSummary = async (): Promise<ApiResponse<Summary>> => {
	const res = await fetch(`${API_BASE_URL}/api/documents/summary`, {
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
	const res = await fetch(`${API_BASE_URL}/api/documents/flashcards`, {
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
	const res = await fetch(`${API_BASE_URL}/api/documents/quiz`, {
		method: "GET",
		credentials: "include",
	});

	const result: ApiResponse<QuizList> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to generate quizzes");
	}

	return result;
};
/**
 * Retrieves Interview Questions from notes
 *
 * @return interview questions
 */
export const getInterviewQuestions = async (): Promise<ApiResponse<Interview>> => {
    const res = await fetch(`${API_BASE_URL}/api/documents/interview`, {
        method: "GET",
        credentials: "include",
    });

    const result: ApiResponse<Interview> = await res.json();

    if (!res.ok) {
        throw new Error(result.message || "Failed to generate interview");
    }

    return result;
};

/**
 * Retrieves grading based on question and user answer
 *
 * @param p0 contains questionId and user's answer to question
 * @return Interview grading response based on answer
 */
export const getInterviewGrading = async (p0: { questionId: number; userAnswer: string | undefined; }): Promise<ApiResponse<InterviewResponse>> => {
    const res = await fetch(`${API_BASE_URL}/api/documents/interview/grade`, {
        method: "POST",
        credentials: "include",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify({
            questionId: p0.questionId,
            userAnswer: p0.userAnswer,
        }),
    });

    const result: ApiResponse<InterviewResponse> = await res.json();

    if (!res.ok) {
        throw new Error(result.message || "Failed to generate interview review");
    }

    return result;
};
