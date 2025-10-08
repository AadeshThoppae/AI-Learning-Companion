'use client'

import ErrorToast from "@/components/ErrorToast";
import FlashcardTab from "@/components/tabs/FlashcardTab";
import QuizTab from "@/components/tabs/QuizTab";
import SummaryTab from "@/components/tabs/SummaryTab";
import Tabs from "@/components/tabs/Tabs";
import UploadTab from "@/components/tabs/UploadTab";
import { Flashcard, Quiz, Summary } from "@/types/documentTypes";
import { useState} from "react";

/**
 * Home component - Main page of the AI Learning Companion application
 * Manages all application state and renders different tabs for document processing,
 * summary generation, and flashcard studying
 * 
 * @returns JSX element containing the complete application interface
 */
export default function Home() {
    const [notes, setNotes] = useState('');
    const [summary, setSummary] = useState<Summary | null>(null);
    const [file, setFile] = useState<File | null>(null);
    const [flashcards, setFlashcards] = useState<Flashcard[] | null>(null);
    const [quizzes, setQuizzes] = useState<Quiz[] | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('Upload');
    const [error, setError] = useState<string>('');

    const tabs = ['Upload','Summary','Flashcards', 'Quiz'];

    /**
     * Resets all application state to initial values
     * Clears uploaded content, generated data, and returns to upload tab
     */
    const handleReset = () =>{
        setNotes('');
        setFile(null);
        setSummary(null);
        setActiveTab('Upload');
        setFlashcards(null);
        setError('');

    }
    return (
    <div className="font-sans items-center justify-items-center min-h-screen p-8 gap-16 w-full">
        {/* Error notification toast */}
        {error && (
            <ErrorToast message={error} onClose={() => setError('')} />
        )}

        {/* Header */}
        <div className="container mx-auto px-4">
            <div className="text-center mb-8">
                <h2 className="text-3xl font-bold text-gray-800 dark:text-white">
                    AI Learning Companion
                </h2>
            </div>
        </div>

        {/* Tabs for navigation */}
        <Tabs activeTab={activeTab} setActiveTab={setActiveTab} tabs={tabs}/>

        {/* Main content area */}
        <div className="mx-auto w-full">
            {/* Upload Tab */}
            {activeTab === 'Upload' && (
                <UploadTab
                    notes={notes} 
                    setNotes={setNotes} 
                    file={file} 
                    setFile={setFile}
                    handleReset={handleReset} 
                    isLoading={isLoading} 
                    setError={setError}
                    setIsLoading={setIsLoading}
                    setActiveTab={setActiveTab}
                    setSummary={setSummary}
                />
            )}
            
            {/* Summary Tab */}
            {activeTab === 'Summary' && (
                <SummaryTab
                    summary={summary}
                    isLoading={isLoading}
                    setSummary={setSummary}
                    setIsLoading={setIsLoading}
                    setError={setError}
                    setActiveTab={setActiveTab}
                    handleReset={handleReset}
                />
            )}

            {/* Flashcards Tab */}
            {activeTab === 'Flashcards' && (
                <FlashcardTab
                    flashcards={flashcards}
                    handleReset={handleReset}
                    setFlashcards={setFlashcards}
                    setError={setError}
                    setActiveTab={setActiveTab}
                />
            )}

            {/* Quiz Tab */}
            {activeTab === 'Quiz' && (
                <QuizTab
                    quizzes={quizzes}
                    handleReset={handleReset}
                    setError={setError}
                    setActiveTab={setActiveTab}
                    setQuizzes={setQuizzes}
                />
            )}
       </div>
    </div>
  );
}
