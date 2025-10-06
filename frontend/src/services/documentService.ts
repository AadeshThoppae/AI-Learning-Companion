import { ApiResponse, FlashcardList, Summary } from "@/types/documentTypes";

const API_BASE_URL = "http://localhost:8080";

export const uploadText = async (text: string): Promise<ApiResponse<null>> => {
	const res = await fetch(`${API_BASE_URL}/api/documents/upload-text`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify({ text }),
		credentials: "include",
	});

	const result: ApiResponse<null> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to upload pdf");
	}

	return result;
};

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

export const getFlashcards = async (): Promise<ApiResponse<FlashcardList>> => {
	const res = await fetch(`${API_BASE_URL}/api/documents/flashcards`, {
		method: "GET",
		credentials: "include",
	});

	const result: ApiResponse<FlashcardList> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "failed to generate flashcards");
	}

	return result;
};
