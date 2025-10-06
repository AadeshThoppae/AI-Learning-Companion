'use client'

import { getFlashcards } from "@/services/documentService";
import { FlashcardList } from "@/types/documentTypes";

interface FlashcardTabProps {
    flashcards: FlashcardList | null;
    handleReset: () => void;
    setFlashcards: (flashcards: FlashcardList | null) => void;
    setIsLoading: (loading: boolean) => void;
    setError: (error: string) => void;
    setActiveTab: (tab: string) => void;
}

export default function FlashcardTab({ flashcards, setFlashcards, handleReset, setIsLoading, setError, setActiveTab }: FlashcardTabProps) {

    const handleGenerateFlashcards = async () => {
        setIsLoading(true);
        setError('');
        setActiveTab('flashcards');

        try {
            const result = await getFlashcards();
            setFlashcards(result.data);
        }catch(e){
            setError(e instanceof Error ? e.message: "failed to generate flashcards");
        }finally {
            setIsLoading(false);
        }
    }

    return (
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
                    AI-Generated Flashcards
                </h2>
                <button
                    onClick={handleReset}
                    className="px-4 py-2 text-sm border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                >
                    New Notes
                </button>
            </div>
            {flashcards && flashcards.flashcards ? (
                <div className="space-y-4">
                    {/*include flashcard implementation here*/}
                </div>
                ) : (
                    <div className="text-center py-12">
                        <p className="text-gray-500 dark:text-gray-400">
                            No flashcards available. Please generate them from the summary.
                        </p>
                    </div>
                )
            }
        </div>
    )
}