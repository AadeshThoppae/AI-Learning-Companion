export type Difficulty = "EASY" | "MEDIUM" | "HARD";

export interface Example {
	input: string;
	output: string;
	explanation: string;
}

export interface TestCase {
	id: number;
	input: string;
	expectedOutput: string;
	hidden: boolean;
}

export interface Constraints {
	timeComplexity: string;
	spaceComplexity: string;
	rules: string[];
}

export interface CodingQuestion {
	id: string;
	topicId: number;
	title: string;
	description: string;
	difficulty: Difficulty;
	examples: Example[];
	methodSignature: string;
	starterCode: string;
	testCases: TestCase[];
	hints?: string;
	constraints: Constraints;
}

export interface CodingQuestionRequest {
	topicId: number;
	difficulty: Difficulty;
}

export interface CodeSubmission {
	questionId: string;
	code: string;
}

export interface TestResult {
	testId: number;
	passed: boolean;
	input: string;
	expectedOutput: string;
	actualOutput: string;
	error: string;
}

export interface ExecutionResult {
	success: boolean;
	passedTests: number;
	totalTests: number;
	results: TestResult[];
	error: string;
	executionTime: number;
}
