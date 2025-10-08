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
 * Container for a collection of flashcards in the format returned by the API
 */
export interface FlashcardList {
	flashcards: Flashcard[];
}

export interface Quiz {
	id: number;
	question: string;
	options: QuizOption[];
	correctAnswer: number;
}

export interface QuizOption {
	id: number;
	option: string;
	why: string;
}

/**
 * Container for a collection of Quizzes in the format returned by the API
 */
export interface QuizList {
	quizzes: Quiz[];
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

export const dummyQuizList: QuizList = {
	quizzes: [
		{
			id: 1,
			question: "What does the 'use client' directive do in a Next.js 13+ component?",
			options: [
				{
					id: 1,
					option: "It marks the component to be rendered only on the server",
					why: "Incorrect, 'use client' actually does the opposite.",
				},
				{
					id: 2,
					option: "It tells Next.js that the component should run on the client side",
					why: "Correct, it ensures the component is rendered in the browser rather than on the server.",
				},
				{
					id: 3,
					option: "It enables server-side data fetching",
					why: "Incorrect, that’s handled by server components or functions like getServerSideProps.",
				},
				{
					id: 4,
					option: "It imports client libraries automatically",
					why: "Incorrect, it doesn’t handle imports.",
				},
			],
			correctAnswer: 2,
		},
		{
			id: 2,
			question: "Which of the following best describes Tailwind CSS?",
			options: [
				{
					id: 1,
					option: "A JavaScript framework for frontend routing",
					why: "Incorrect, Tailwind CSS is not a JS framework.",
				},
				{
					id: 2,
					option: "A utility-first CSS framework for rapidly building custom designs",
					why: "Correct, Tailwind provides utility classes to style elements quickly.",
				},
				{
					id: 3,
					option: "A component library similar to Material UI",
					why: "Incorrect, Tailwind focuses on styling utilities, not prebuilt components.",
				},
				{
					id: 4,
					option: "A CSS preprocessor like Sass",
					why: "Incorrect, Tailwind doesn’t preprocess CSS but generates utilities from a config.",
				},
			],
			correctAnswer: 2,
		},
		{
			id: 3,
			question: "In TypeScript, what does the '?' symbol after a property name mean?",
			options: [
				{
					id: 1,
					option: "It marks the property as private",
					why: "Incorrect, TypeScript uses 'private' keyword for that.",
				},
				{
					id: 2,
					option: "It makes the property optional",
					why: "Correct, '?' indicates that the property may be undefined.",
				},
				{
					id: 3,
					option: "It defines a default value for the property",
					why: "Incorrect, default values are set in constructors or object literals.",
				},
				{
					id: 4,
					option: "It marks the property as read-only",
					why: "Incorrect, 'readonly' keyword is used for that.",
				},
			],
			correctAnswer: 2,
		},
		{
			id: 4,
			question: "What does 'npm run build' typically do in a Next.js project?",
			options: [
				{ id: 1, option: "Runs the development server", why: "Incorrect, that’s 'npm run dev'." },
				{
					id: 2,
					option: "Builds the project for production",
					why: "Correct, it compiles and optimizes your app for deployment.",
				},
				{ id: 3, option: "Installs dependencies", why: "Incorrect, that’s done with 'npm install'." },
				{
					id: 4,
					option: "Cleans up old build files",
					why: "Incorrect, though it may overwrite old builds, that’s not its main purpose.",
				},
			],
			correctAnswer: 2,
		},
		{
			id: 5,
			question:
				"Which file in a Next.js project defines custom app-wide configuration, such as providers or layouts?",
			options: [
				{
					id: 1,
					option: "layout.tsx",
					why: "Correct, layouts define shared UI and can wrap pages or components.",
				},
				{
					id: 2,
					option: "next.config.js",
					why: "Incorrect, that file is for build-time configuration, not layouts.",
				},
				{
					id: 3,
					option: "_document.tsx",
					why: "Incorrect, _document is used to customize the HTML structure, not layouts.",
				},
				{ id: 4, option: "middleware.ts", why: "Incorrect, middleware handles request logic, not UI." },
			],
			correctAnswer: 1,
		},
	],
};
