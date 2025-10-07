import type { Flashcard } from "@/types/documentTypes";
import { useState } from "react";
import { FaRegLightbulb } from "react-icons/fa6";

interface FlashcardProps {
    flashcard: Flashcard;
}

export default function Flashcard({ flashcard } : FlashcardProps) {
    const [showHint, setShowHint] = useState(false);
    const [rotationDegrees, setRotationDegrees] = useState(0);

    const handleFlip = () => {
        setRotationDegrees(prev => prev + 180);
    };

    const isFlipped = Math.floor(rotationDegrees / 180) % 2 === 1;

    return (
        <button className="w-xl h-96 outline-none perspective-[1000px]" onClick={handleFlip}>
            <div className={`relative size-full transition duration-500 transform-3d ease`} style={{ transform: `rotateX(${rotationDegrees}deg)` }}>
                <div className="absolute inset-0 size-full backface-hidden bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 p-6 rounded-xl border-l-4 border-blue-500
                                shadow-lg mb-4 hover:scale-[1.02] transition-transform cursor-pointer flex flex-col justify-around items-center">
                    <h3 className="text-2xl font-semibold text-gray-800 dark:text-white mb-2 text-center ">{flashcard.id}. {flashcard.question}</h3>
                    
                    <button onClick={(e) => {
                        e.stopPropagation(); // Prevent the flip when clicking the hint button
                        setShowHint(!showHint)
                        }}>
                        {showHint ? (
                            <div className="flex items-center font-semibold gap-2 py-2 px-4 bg-gradient-to-r from-green-400/50 to-green-500/50 text-white rounded-lg cursor-pointer">
                                {flashcard.hint}                    
                            </div>
                        ) : (    
                            <div className="flex items-center font-semibold gap-2 py-2 px-4 opacity-70 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:opacity-100 hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105 cursor-pointer">
                                <FaRegLightbulb className="" size={18} />
                                Get a hint
                            </div>
                        )
                        }
                    </button>
                    
                </div>
                <div className="absolute inset-0 size-full backface-hidden rotate-x-180 bg-gradient-to-r from-blue-50 to-indigo-50 dark:from-blue-900/20 dark:to-indigo-900/20 p-6 rounded-xl border-l-4 border-blue-500
                    text-white shadow-lg mb-4 hover:scale-[1.02] transition-transform cursor-pointer flex flex-col justify-around items-center">
                    <h3 className="text-2xl font-semibold text-gray-800 dark:text-white mb-2 text-center">{flashcard.answer}</h3>
                    <div className="flex flex-col items-center gap-4 font-semibold">
                        Did you get it right?
                        <div className="flex gap-4 w-96 justify-around">
                            <button className="flex justify-center font-semibold gap-2 w-1/3 py-2 opacity-70 bg-gradient-to-r 
                                from-purple-500 to-pink-600 text-white rounded-lg hover:opacity-100 hover:from-purple-600 hover:to-pink-700 
                                transition-all transform hover:scale-105 cursor-pointer"
                                onClick={(e) => { e.stopPropagation(); }}
                            >
                                NO
                            </button>
                            <button className="flex justify-center font-semibold gap-2 w-1/3 py-2 opacity-70 bg-gradient-to-r 
                                from-purple-500 to-pink-600 text-white rounded-lg hover:opacity-100 hover:from-purple-600 hover:to-pink-700 
                                transition-all transform hover:scale-105 cursor-pointer"
                                onClick={(e) => { e.stopPropagation(); }}
                            >
                                YES
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </button>
    );
}