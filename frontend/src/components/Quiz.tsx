import type { Quiz } from "@/types/documentTypes";
import { useEffect, useState } from "react";

interface QuizProps {
    quiz: Quiz;
}


export default function Quiz({ quiz }: QuizProps) {
    const [isAnswered, setIsAnswered] = useState<boolean>(false);

    useEffect(() => {
        setIsAnswered(false); // Reset answer state when a new quiz is loaded
    }, [quiz]);

    const handleOptionClick = () => {
        if (isAnswered) return;
        
        setIsAnswered(true);
    };

    return (
        <div className="w-3xl min-h-96 mx-auto flex flex-col justify-between bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 p-6 rounded-xl border-l-4 border-blue-500">
            <h3 className="text-2xl font-semibold text-gray-800 dark:text-white py-4 text-center">
                {quiz.id}. {quiz.question}
            </h3>
            <div className={`flex flex-col gap-2 ${isAnswered && 'gap-4'}`}>
                {quiz.options.map((option) => (
                    <div key={option.id} className={`shadow-xl flex flex-col gap-2 bg-white dark:bg-gray-800 rounded-lg p-4 border border-gray-300 dark:border-gray-600
                        ${!isAnswered && 'hover:bg-gray-50 dark:hover:bg-gray-700 cursor-pointer'}
                        ${isAnswered && 'cursor-not-allowed opacity-90'}
                        transition-all
                        ${isAnswered && 
                            (option.id === quiz.correctAnswer
                                ? 'ring-2 ring-green-300 dark:ring-green-500'
                                : 'ring-2 ring-red-300 dark:ring-red-700'
                            )
                        }`}
                        onClick={handleOptionClick}
                        >
                        <div className="flex gap-2 font-semibold">
                            <span>{option.id}. </span>
                            <span>{option.option}</span>
                        </div>
                        {isAnswered && isAnswered && (
                            <div className="flex flex-col transition-all">
                                <span className={`font-semibold ${
                                    option.id === quiz.correctAnswer
                                        ? 'text-green-500 dark:text-green-300'
                                        : 'text-red-800 dark:text-red-200'
                                    }`}>
                                    {option.id === quiz.correctAnswer ? (
                                        <div className="flex items-center gap-4">
                                            <span>✓</span>
                                            <span>Right Answer</span>
                                        </div>
                                    ) : (
                                        <div className="flex items-center gap-4">
                                            <span>✗</span>
                                            <span>Not quite.</span>
                                        </div>
                                    )}
                                </span>
                                <span className="ml-6 mt-1">
                                    {option.why}    
                                </span>
                            </div>
                        )}
                    </div>
                ))}
            </div>
        </div>
    );
}