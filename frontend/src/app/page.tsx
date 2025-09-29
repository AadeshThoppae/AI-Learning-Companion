'use client'

import Image from "next/image";
import {useState} from "react";

export default function Home() {
    const [notes, setNotes] = useState('');
    const [summary, setSummary] = useState('');
    const [isLoading, setisLoading] = useState(false);
    const [activeTab, setActiveTab] = useState('upload');


    const handleNotesSubmit = async () => {
        if(!notes.trim()) return;

        setActiveTab("summary");
        setisLoading(true);

        // AI Processing link here
    }
    const handleReset = () =>{
        setNotes('');
        setSummary('');
        setActiveTab('upload');

    }

  return (
    <div className="font-sans items-center justify-items-center min-h-screen p-8 pb-20 gap-16 sm:p-20">
      <div className={"container mx-auto px-4 py-8"}>
          <div className={"text-center mb-8"}>
              <div className={"flex items-center justify gap-3 mb-4"}>
                  <span className={"text-white font-bold text-lg"}>
                      AI
                  </span>
              </div>
              <h1 className={"text-3xl font-bold text-gray-800 dark:text-white"}>
                  Learning Companion
              </h1>
          </div>
      </div>
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
                    disabled={!summary && !isLoading}
                >
                    Summary
                </button>
            </div>
        </div>
        <div className="max-w-4xl mx-auto">
            {activeTab === 'upload' && (
                <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8">
                    <h2 className="text-2xl font-semibold text-gray-800 dark:text-white mb-6">
                        Upload Your Notes
                    </h2>

                    <div className="space-y-6">
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
                        </div>

                        <div className="flex items-center justify-between gap-3">
                            <div className="text-sm text-gray-500 dark:text-gray-400">
                                {notes.length} characters
                            </div>
                            <div className="flex gap-3">
                                <button
                                    onClick={handleReset}
                                    className="px-6 py-2 border border-gray-300 dark:border-gray-600 text-gray-700 dark:text-gray-300 rounded-lg hover:bg-gray-50 dark:hover:bg-gray-700 transition-colors"
                                    disabled={isLoading}
                                >
                                    Clear
                                </button>
                                <button
                                    onClick={handleNotesSubmit}
                                    disabled={!notes.trim() || isLoading}
                                    className="px-6 py-2 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105"
                                >
                                    {isLoading ? 'Processing...' : 'Generate Summary'}
                                </button>
                            </div>
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
                                <div className="whitespace-pre-wrap text-gray-700 dark:text-gray-300">
                                    {summary}
                                </div>
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
        </div>


    </div>
  );
}
