'use client'

import { Quiz as QuizType } from "@/types/documentTypes";
import { useState } from "react";
import Quiz from "../Quiz";
import { getQuizzes } from "@/services/documentService";

/**
 * Props interface for the QuizTab component
 */
interface QuizTabProps {
    quizzes: QuizType[] | null;
    handleReset: () => void;
    setError: (error: string) => void;
    setActiveTab: (tab: string) => void;
    setQuizzes: (quizzes: QuizType[]) => void;
}

/**
 * QuizTab component that displays and manages AI-generated quiz questions.
 * Provides navigation between questions and handles quiz generation.
 * 
 * @param props - The component props
 * @returns A rendered quiz tab with navigation controls
 */
export default function QuizTab({ quizzes, handleReset, setError, setActiveTab, setQuizzes }: QuizTabProps) {
    const [isLoading, setIsLoading] = useState(false);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [currentQuizzes, setCurrentQuizzes] = useState<QuizType[] | null>(quizzes);

    /**
     * Handles the generation of new quiz questions by calling the API.
     * Updates loading state, error messages, and quiz data.
     */
    const handleGenerateQuiz = async () => {
        setIsLoading(true);
        setError('');
        setActiveTab('Quiz');

        try {
            const result = await getQuizzes();

            setQuizzes(result.data?.quiz ?? []);
            setCurrentQuizzes(result.data?.quiz ?? []);
        } catch (e) {
            setError(e instanceof Error ? e.message : "Failed to generate quiz.");
        }finally {
            setIsLoading(false);
        }
    }

    return (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8 mx-auto w-2/3 ">
            <div className="flex items-center justify-between gap-2 mb-6">
                <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
                    AI-Generated Quiz
                </h2>
                <button
                    onClick={handleReset}
                    className="px-4 py-2 text-sm border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors cursor-pointer"
                >
                    New Notes
                </button>
            </div>

            {isLoading ? (
                <div className="flex items-center justify-center py-12">
                    <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500"></div>
                        <span className="ml-4 text-gray-600 dark:text-gray-300">
                            Loading ...
                        </span>
                </div>
            ) : currentQuizzes ? (
                <div>
                    {/* Current Quiz */}
                    <Quiz quiz={currentQuizzes[currentIndex]} />
                    {/* Navigation Buttons */}
                    <div className="flex w-3xl mx-auto justify-end gap-8 mt-6">
                        <button
                            onClick={() => setCurrentIndex((prev) => Math.max(prev - 1, 0))}
                            disabled={currentIndex === 0}
                            className="px-6 py-2 shadow-xl bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                        >
                            Previous
                        </button>
                        <button
                            onClick={() => setCurrentIndex((prev) => Math.min(prev + 1, (currentQuizzes?.length ?? 1) - 1))}
                            disabled={currentIndex === (currentQuizzes?.length ?? 1) - 1}
                            className="px-6 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                        >
                            Next
                        </button>
                    </div>
                </div>
            ) : (
                /* No quiz available */
                <div className="text-center py-12 gap-4 flex flex-col items-center justify-center">
                    <p className="text-gray-500 dark:text-gray-400">
                        No quiz available.
                    </p>
                    <button
                        onClick={handleGenerateQuiz}
                        disabled={isLoading}
                        className="px-6 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                    >
                        {isLoading ? 'Processing...' : 'Generate Quiz'}
                    </button>
                </div>
                )
            }
        </div>
    )
}