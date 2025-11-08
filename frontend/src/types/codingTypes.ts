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
