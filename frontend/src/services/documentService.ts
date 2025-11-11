import { ApiResponse } from "@/types/documentTypes";

/** Base URL for the API endpoints */
export const API_BASE_URL = "http://localhost:8080";

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
