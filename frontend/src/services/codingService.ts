import { ApiResponse } from "@/types/documentTypes";
import { API_BASE_URL } from "./documentService";
import {
	CodeSubmission,
	CodingQuestion,
	CodingQuestionRequest,
	CodingTopicList,
	ExecutionResult,
} from "@/types/codingTypes";

export const getCodingTopics = async (): Promise<ApiResponse<CodingTopicList>> => {
	const res = await fetch(`${API_BASE_URL}/api/coding/coding-topics`, {
		method: "GET",
		headers: { "Content-Type": "application/json" },
		credentials: "include",
	});

	const result: ApiResponse<CodingTopicList> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to fetch coding topics");
	}

	return result;
};

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

export const testCode = async (codeSubmission: CodeSubmission): Promise<ApiResponse<ExecutionResult>> => {
	const res = await fetch(`${API_BASE_URL}/api/coding/coding-question/test`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(codeSubmission),
		credentials: "include",
	});

	const result: ApiResponse<ExecutionResult> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to test code");
	}

	return result;
};

export const submitCode = async (codeSubmission: CodeSubmission): Promise<ApiResponse<ExecutionResult>> => {
	const res = await fetch(`${API_BASE_URL}/api/coding/coding-question/submit`, {
		method: "POST",
		headers: { "Content-Type": "application/json" },
		body: JSON.stringify(codeSubmission),
		credentials: "include",
	});

	const result: ApiResponse<ExecutionResult> = await res.json();

	if (!res.ok) {
		throw new Error(result.message || "Failed to test code");
	}

	return result;
};
