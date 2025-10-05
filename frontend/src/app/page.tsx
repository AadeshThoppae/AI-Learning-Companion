'use client'

import {useState} from "react";

interface Summary {
    title: string;
    keyPoints: string[];
}

interface Flashcard {
    id: number;
    question: string;
    answer: string;
    hint: string;
}

interface FlashcardList {
    flashcards: Flashcard[];
}

interface ApiResponse<T> {
    message: string;
    code: string;
    data: T | null;
}
export default function Home() {
    const [notes, setNotes] = useState('');
    const [summary, setSummary] = useState<Summary | null>(null);
    const [file, setFile] = useState<File | null>(null);
    const [flashcards,setFlashcards] = useState<FlashcardList | null>(null);
    const [isLoading, setisLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('upload');
    const [uploadMode, setUploadMode] = useState<'text' | 'pdf'>('text');
    const [error, setError] = useState<string>('');

    const API_BASE_URL = 'http://localhost:8080';

    const handleTextUpload = async () => {
        if(!notes.trim()) return;

        //setActiveTab("summary");
        setisLoading(true);


        try {
            const response = await fetch (`${API_BASE_URL}/api/documents/upload-text`,{
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({text: notes}),
                credentials: 'include',
            });

            const result: ApiResponse<null> = await response.json();

            if(!response.ok){
                throw new Error(result.message || 'Failed to upload text');
            }
            await handleGenerateSummary();
        }catch (e) {
            setError(e instanceof Error ? e.message : "text upload failed");
            setisLoading(false);
        }
    }

    const handlePDFUpload = async () => {
        if(!file){
            return;
        }
        setisLoading(true);
        setError('');

        try {
            const formData = new FormData();
            formData.append('file', file);

            const response = await fetch(`${API_BASE_URL}/api/documents/upload`, {
                method: 'POST',
                body: formData,
                credentials: 'include',
            });

            const res: ApiResponse<null> =await response.json();
            if(!response.ok){
                throw new Error(res.message || 'Failed to upload PDF');
            }

            await handleGenerateSummary();
        }catch(e){
            setError(e instanceof Error ? e.message : "failure to upload PDF");
            setisLoading(false);
        }
    }

    const handleNotesSubmit = () => {
        if(uploadMode === 'text'){
            handleTextUpload();
        }else{
            handlePDFUpload();
        }
    }
    const handleGenerateSummary = async () => {
        setisLoading(true);
        setError(' ');
        setActiveTab('summary');

        try {
            const response = await fetch(`${API_BASE_URL}/api/documents/summary`, {
                method: 'POST',
                credentials: 'include',
            });

            const result: ApiResponse<Summary> = await response.json();

            if(!response.ok){
                throw new Error(result.message || 'Failed to generate summary');
            }

            setSummary(result.data);
        }catch(e){
            setError(e instanceof Error ? e.message: 'Failed to generate summary');
        }finally {
            setisLoading(false);
        }
    };

    const handleGenerateFlashcards = async () => {
        setisLoading(true);
        setError('');
        setActiveTab('flashcards');

        try {
            const response = await fetch(`${API_BASE_URL}/api/documents/flashcards`, {
                method: 'POST',
                credentials: 'include',
            });
            const result: ApiResponse<FlashcardList> = await response.json();

            if(!response.ok){
                throw new Error(result.message || "failed to generate flashcards");
            }

            setFlashcards(result.data);
        }catch(e){
            setError(e instanceof Error ? e.message: "failed to generate flashcards");
        }finally {
            setisLoading(false);
        }
    }
    //const canSubmit = uploadMode === 'text' ? notes.trim().length > 0 : file !== null;

    const handleReset = () =>{
        setNotes('');
        setFile(null);
        setSummary(null);
        setActiveTab('upload');
        setFlashcards(null);
        setError('');

    }
    const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        if (e.target.files && e.target.files[0]) {
            setFile(e.target.files[0]);
        }
    };

    return (
    <div className="font-sans items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20">
      <div className="container mx-auto px-4 py-8">
          <div className="text-center mb-8">
              <h2 className="text-3xl font-bold text-gray-800 dark:text-white">
                  AI Learning Companion
              </h2>
          </div>
      </div>
        {/*Tabs*/}
        <div className={"flex justify-center mb-8"}>
            <div className={"bg-white dark:bg-gray-800 rounded-lg p-1 shadow-lg"}>
                <button
                    onClick={() => setActiveTab('upload')}
                    className={`px-6 py-2 rounded-md font-medium transition-all ${
                        activeTab === 'upload'
                            ? 'bg-blue-500 text-white shadow-md'
                            : 'text-gray-600 dark:text-gray-300 hover:text-blue-500'
                    }`}
                >
                    Upload Notes
                </button>
                <button
                    onClick={() => setActiveTab('summary')}
                    className={`px-6 py-2 rounded-md font-medium transition-all ${
                        activeTab === 'summary'
                            ? 'bg-blue-500 text-white shadow-md'
                            : 'text-gray-600 dark:text-gray-300 hover:text-blue-500'
                    }`}
                    //disabled={!summary && !isLoading}
                >
                    Summary
                </button>
                <button
                    onClick={() => setActiveTab('flashcards')}
                    className={`px-6 py-2 rounded-md font-medium transition-all ${
                        activeTab === 'flashcards'
                            ? 'bg-blue-500 text-white shadow-md'
                            : 'text-gray-600 dark:text-gray-300 hover:text-blue-500'
                    }`}
                    //disabled={!flashcards && !isLoading}
                >
                    Flashcards
                </button>
            </div>
        </div>
        <div className="max-w-4xl mx-auto">
            {/*Upload Tab*/}
            {activeTab === 'upload' && (
                <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8">
                    <h2 className="text-2xl font-semibold text-gray-800 dark:text-white mb-6">
                        Upload Your Notes
                    </h2>
                    {/*Upload mode*/}
                    <div className={"flex justify-center mb-6"}>
                        <div className="bg-gray-100 dark:bg-gray-700 rounded-lg p-1">
                            <button onClick={()=>{
                                setUploadMode('text');
                                setError('');
                            }} className={`px-6 py-2 rounded-md font-medium transition-all ${
                                uploadMode === 'text' ? 'bg-white dark:bg-gray-600 shadow-md': 'text-gray-600 dark:text-gray-300'
                            }`}>
                                Paste Text 📝
                            </button>
                            <button
                                onClick={() => {
                                    setUploadMode('pdf');
                                    setError('');
                                }} className={`px-6 py-2 rounded-md font-medium transition-all ${
                                    uploadMode === 'pdf'
                                        ? 'bg-white dark:bg-gray-600 shadow-md'
                                        : 'text-gray-600 dark:text-gray-300'
                                }`}
                            >
                                Upload PDF 📄
                            </button>
                        </div>
                    </div>

                    <div className="space-y-6">
                        {uploadMode === 'text' ? (
                            // Text Mode
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                    Paste your lecture notes, slides, or study material below:
                                </label>
                                <textarea
                                    value={notes}
                                    onChange={(e) => setNotes(e.target.value)}
                                    placeholder="Paste your notes here... You can include lecture transcripts, slide content, textbook excerpts, or any study material you'd like to transform into learning resources."
                                    className="w-full h-64 p-4 border border-gray-300 dark:border-gray-600 rounded-lg resize-none focus:ring-2 focus:ring-blue-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                                />
                                <div className="mt-2 text-sm text-gray-500 dark:text-gray-400">
                                    {notes.length} characters
                                </div>
                            </div>
                        ) : (
                            // PDF mode
                            <div>
                                <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                    Upload a PDF document:
                                </label>
                                <div className="flex items-center justify-center w-full">
                                    <label className="flex col items-center justify-center w-full h-64 border-2 border-gray-300 border-dashed rounded-lg cursor-pointer bg-gray-50 dark:bg-gray-700 hover:bg-gray-100 dark:hover:bg-gray-600 transition-colors">
                                        <div className="flex flex-col items-center justify-center pt-5 pb-6">
                                            <p className="mb-2 text-sm text-gray-500 dark:text-gray-400">
                                                <span className="font-semibold">Click to upload</span> or drag and drop
                                            </p>
                                            <p className="text-xs text-gray-500 dark:text-gray-400">PDF files only</p>
                                        </div>
                                        <input
                                            type="file"
                                            accept=".pdf"
                                            onChange={handleFileChange}
                                            className="hidden"
                                        />
                                    </label>
                                </div>
                                {file &&(
                                    <div className="mt-4 p-4 bg-blue-50 dark:bg-blue-900/20 rounded-lg border border-blue-200 dark:border-blue-800">
                                        <div className="flex items-center gap-2">
                                            <span className="text-sm text-gray-700 dark:text-gray-300">
                                                    {file.name}
                                                </span>
                                        </div>
                                    </div>
                                )}
                            </div>
                        )}


                        <div className="flex items-center justify-between gap-3">
                            <button
                                onClick={handleReset}
                                className="px-6 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                                disabled={isLoading}
                            >
                                Clear
                            </button>
                            <button
                                onClick={handleNotesSubmit}
                                disabled={isLoading}
                                className="px-6 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105"
                            >
                                {isLoading ? 'Processing...' : 'Generate Summary'}
                            </button>
                        </div>
                    </div>
                </div>
            )}
            {activeTab === 'summary' && (
                <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8">
                    <div className="flex items-center justify-between mb-6">
                        <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
                            AI-Generated Summary
                        </h2>
                        <button
                            onClick={handleReset}
                            className="px-4 py-2 text-sm border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
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
                                    {summary.title}
                                </h3>
                                <ul className="space-y-2">
                                    {summary.keyPoints.map((point, index) => (
                                        <li key={index} className="text-gray-700 dark:text-gray-300">
                                            {point}
                                        </li>
                                    ))}
                                </ul>
                            </div>


                            <div className="mt-8 flex gap-4">
                                <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105">
                                    Generate Flashcards
                                </button>
                                <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105">
                                    Create Quiz
                                </button>
                                <button className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105">
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
            )}
            {/* Flashcards Tab */}
            {activeTab === 'flashcards' && (
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
                )}
            </div>
        )}


       </div>
    </div>
  );
}
