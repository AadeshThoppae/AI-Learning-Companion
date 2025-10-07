'use client'

import { getFlashcards } from "@/services/documentService";
import { Flashcard as FlashcardType } from "@/types/documentTypes";
import { useEffect, useState } from "react";
import Flashcard from "@/components/Flashcard";

interface FlashcardTabProps {
    flashcards: FlashcardType[] | null;
    handleReset: () => void;
    setFlashcards: (flashcards: FlashcardType[]) => void;
    setError: (error: string) => void;
    setActiveTab: (tab: string) => void;
}

export default function FlashcardTab({ flashcards, setFlashcards, handleReset, setError, setActiveTab }: FlashcardTabProps) {
    const [isLoading, setIsLoading] = useState(false);
    const [score, setScore] = useState<{ incorrect: number; correct: number; }>({ incorrect: 0, correct: 0 });
    const [currentIndex, setCurrentIndex] = useState(0);

    // Fetch Flashcards on load
        /* useEffect(() => {
            if (flashcards) return;
    
            const fetchFlashcards = async () => {
                setIsLoading(true);
                try {
                    const existingFlashcards = await getFlashcards(); // Your API call
                    if (existingFlashcards) {
                        setFlashcards(existingFlashcards.data);
                    }
                } catch (err) {
                    console.error("Failed to fetch flashcards:", err);
                    setError('Could not load existing flashcards.');
                } finally {
                    setIsLoading(false);
                }
            }
    
            fetchFlashcards();
        }, [setError, setIsLoading, setFlashcards, flashcards]); */

    const handleGenerateFlashcards = async () => {
        setIsLoading(true);
        setError('');
        setActiveTab('flashcards');

        try {
            const result = await getFlashcards();
            setFlashcards(result.data?.flashcards);
        }catch(e){
            setError(e instanceof Error ? e.message: "failed to generate flashcards");
        }finally {
            setIsLoading(false);
        }
    }

    return (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8 mx-auto w-2/3 ">
            <div className="flex items-center justify-between gap-2 mb-6">
                <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
                    AI-Generated Flashcards
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
            ) : flashcards && flashcards ? (
                <div className="flex flex-col items-center justify-center">
                    {/* Score board */}
                    <div className="grid grid-cols-3 w-xl items-center mb-4 text-center text-gray-700 dark:text-gray-300 font-semibold text-sm">
                        <div className="flex gap-2 text-orange-400 items-center justify-start">
                            <div className="px-3 border-orange-400 border-1 rounded-full text-sm leading-6">
                                {score.incorrect} 
                            </div>
                            Still Learning
                        </div>
                        {/* Progress indicator - centered */}
                        <div className="text-sm text-gray-600 dark:text-gray-400">
                            Card {currentIndex + 1 < flashcards.length ? currentIndex + 1 : currentIndex} of {flashcards.length}
                        </div>
                        <div className="flex gap-2 text-green-400 items-center justify-end">
                            Mastered
                            <div className="px-3 border-green-400 border-1 rounded-full text-sm leading-6">
                                {score.correct}
                            </div>
                        </div>
                    </div>
                    
                    {/* Current Flashcard */}
                    {currentIndex < flashcards.length ? (
                        <Flashcard flashcard={flashcards[currentIndex]} score={score} setScore={setScore} currentIndex={currentIndex} setCurrentIndex={setCurrentIndex} />
                    ) : (
                        <div className="w-full text-center py-12 gap-4 flex flex-col items-center justify-center">
                            <p className="text-gray-500 dark:text-gray-400">
                                You&apos;ve completed all flashcards! Great job! 🎉
                            </p>
                            <div className="flex gap-8 w-1/2 justify-center">
                                <button
                                    className="mt-4 w-1/2 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                                >
                                    Focus on {score.incorrect} learning cards
                                </button>
                                <button
                                    className="mt-4 w-1/2 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                                >
                                    Restart Flashcards
                                </button>
                            </div>
                        </div>
                    )}
                </div>
                ) : (
                    <div className="text-center py-12 gap-4 flex flex-col items-center justify-center">
                        <p className="text-gray-500 dark:text-gray-400">
                            No flashcards available.
                        </p>
                        <button
                            onClick={handleGenerateFlashcards}
                            disabled={isLoading}
                            className="px-6 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                        >
                            {isLoading ? 'Processing...' : 'Generate Summary'}
                        </button>
                    </div>
                )
            }
        </div>
    )
}