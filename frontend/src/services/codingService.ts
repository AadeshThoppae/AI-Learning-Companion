import { ApiResponse } from "@/types/documentTypes";
import { API_BASE_URL } from "./documentService";
import { CodingQuestion, CodingQuestionRequest } from "@/types/codingTypes";

export const getCodingQuestion = async (
	codingQuestionRequest: CodingQuestionRequest
): Promise<ApiResponse<CodingQuestion>> => {
	const res = await fetch(`${API_BASE_URL}/api/coding/coding-question`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(codingQuestionRequest),
		credentials: "include",
	});

	const result: ApiResponse<CodingQuestion> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to generate coding question");
	}

	return result;
};
