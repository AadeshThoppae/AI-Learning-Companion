'use client'

import FlashcardTab from "@/components/tabs/FlashcardTab";
import SummaryTab from "@/components/tabs/SummaryTab";
import Tabs from "@/components/tabs/Tabs";
import UploadTab from "@/components/tabs/UploadTab";
import { getFlashcards } from "@/services/documentService";
import { FlashcardList, Summary } from "@/types/documentTypes";
import {useState} from "react";


export default function Home() {
    const [notes, setNotes] = useState('');
    const [summary, setSummary] = useState<Summary | null>(null);
    const [file, setFile] = useState<File | null>(null);
    const [flashcards,setFlashcards] = useState<FlashcardList | null>(null);
    const [isLoading, setIsLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('upload');
    const [error, setError] = useState<string>('');

    const tabs = ['upload','summary','flashcards'];

    //const canSubmit = uploadMode === 'text' ? notes.trim().length > 0 : file !== null;

    const handleReset = () =>{
        setNotes('');
        setFile(null);
        setSummary(null);
        setActiveTab('upload');
        setFlashcards(null);
        setError('');

    }

    return (
    <div className="font-sans items-center justify-items-center min-h-screen p-8 gap-16">
      <div className="container mx-auto px-4">
          <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-gray-800 dark:text-white">
                  AI Learning Companion
              </h2>
          </div>
      </div>
        <Tabs activeTab={activeTab} setActiveTab={setActiveTab} tabs={tabs}/>
        <div className="max-w-4xl mx-auto">
            {activeTab === 'upload' && (
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
            
            {activeTab === 'summary' && (
                <SummaryTab
                    summary={summary}
                    isLoading={isLoading}
                    handleReset={handleReset}
                />
            )}
            {/* Flashcards Tab */}
            {activeTab === 'flashcards' && (
                <FlashcardTab
                    flashcards={flashcards}
                    handleReset={handleReset}
                    setFlashcards={setFlashcards}
                    setIsLoading={setIsLoading}
                    setError={setError}
                    setActiveTab={setActiveTab}
                />
            )}
       </div>
    </div>
  );
}
