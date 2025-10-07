'use client'

import { getSummary } from "@/services/documentService";
import { Summary } from "@/types/documentTypes";
import { useEffect } from "react";
import MarkdownRenderer from "../MarkdownRenderer";

/**
 * Props interface for the SummaryTab component
 */
interface SummaryTabProps {
    summary: Summary | null;
    isLoading: boolean;
    setSummary: (summary: Summary | null) => void;
    setIsLoading: (loading: boolean) => void;
    setError: (error: string) => void;
    setActiveTab: (tab: string) => void;
    handleReset: () => void;
}

/**
 * SummaryTab component for displaying AI-generated document summaries
 * Automatically fetches existing summary on mount and provides navigation to other learning modes
 * 
 * @param props - The component props containing summary data and state management functions
 * @returns JSX element containing the summary interface with key points and action buttons
 */
export default function SummaryTab({ summary, isLoading, setSummary, setIsLoading, setError, setActiveTab, handleReset }: SummaryTabProps) {

    // Fetch summary on load
    useEffect(() => {
        if (summary) return; // If summary already exists, do not fetch again

        /**
         * Asynchronous function to fetch summary from the API
         * Handles loading states and error conditions
         */
        const fetchSummary = async () => {
            setIsLoading(true);
            try {
                const existingSummary = await getSummary(); // Your API call
                if (existingSummary) {
                    setSummary(existingSummary.data);
                }
            } catch (err) {
                console.error("Failed to fetch summary:", err);
                setError('Could not load existing summary.');
            } finally {
                setIsLoading(false);
            }
        }

        fetchSummary();
    }, [setError, setIsLoading, setSummary, summary]);

    return (
    <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8 mx-auto w-2/3">
        <div className="flex items-center justify-between mb-6">
            <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
                AI-Generated Summary
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
        ) : summary ? (
            <div className="prose dark:prose-invert max-w-none">
                <div className="bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 p-6 rounded-lg border-l-4 border-blue-500">
                    <h3 className="text-xl font-semibold mb-4 text-gray-800 dark:text-white">
                        <MarkdownRenderer content={summary.title} />
                    </h3>
                    <ul className="space-y-2">
                        {summary.keyPoints.map((point, index) => (
                            <li key={index} className="text-gray-700 dark:text-gray-300">
                                <MarkdownRenderer content={point} />
                            </li>
                        ))}
                    </ul>
                </div>

                {/* Action buttons for other learning modes */}
                <div className="mt-8 flex gap-4">
                    <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg
                        hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105 cursor-pointer"
                        onClick={() => setActiveTab('flashcards')}>
                        Generate Flashcards
                    </button>
                    <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105 cursor-pointer">
                        Create Quiz
                    </button>
                    <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105 cursor-pointer">
                        Coding Problems
                    </button>
                </div>
            </div>
        ) : (
            <div className="text-center py-12">
                <p className="text-gray-500 dark:text-gray-400">
                    No summary available. Please upload your notes first.
                </p>
            </div>
        )}
    </div>
    );
}