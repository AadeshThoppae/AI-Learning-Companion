import MarkdownRenderer from "@/components/MarkdownRenderer";
import {InterviewQuestion, InterviewResponse} from "@/types/documentTypes";
import {useState} from "react";
import {getInterviewGrading, getInterviewQuestions} from "@/services/documentService";

interface InterviewTabProps{
    interview: InterviewQuestion[] | null;
    handleReset: () => void;
    setError:(error: string) => void;
    setActiveTab:(tab:string) => void;
    setInterview: (interview: InterviewQuestion[]) => void;
}

type SelfGrade = 'good' | 'okay' | 'bad' | null;

type GradingMode = 'self' | 'ai';



export default function InterviewTab({
    interview, handleReset, setError, setActiveTab, setInterview
                                     }: InterviewTabProps){
    const [isLoading,setIsLoading] = useState(false);
    const [currentInterview,setcurrentInterview] = useState<InterviewQuestion[] | null>(interview);
    const [revealedAnswers,setrevealedAnswers] = useState<Set<number>>(new Set());
    const [selfGrades,setSelfGrades] = useState<Map<number,SelfGrade>>(new Map());
    const [gradingMode,setGradingMode] = useState<Map<number,GradingMode>>(new Map());
    const [userAnswers,setUserAnswers] = useState<Map<number,string>>(new Map());
    const [aiGradings,setaiGradings] = useState<Map<number,InterviewResponse>>(new Map());
    const [gradingQuestionId,setGradingQuestionId] = useState<number | null>(null);

    /**
     * Generates interviews once button is clicked
     */
    const handleGenerateInterview = async () =>{
        setIsLoading(true);
        setError('');
        setActiveTab('Interview');

        try{
            const result = await getInterviewQuestions();
            setInterview(result.data?.questions ?? []);
            setcurrentInterview(result.data?.questions ?? []);
            setrevealedAnswers(new Set());
            setSelfGrades(new Map());
            setGradingMode(new Map());
            setUserAnswers(new Map());
            setaiGradings(new Map());
        }catch (e){
            setError(e instanceof Error ? e.message: "Failed to generate interview questions");
        }finally {
            setIsLoading(false);
        }
    };
    /**
     * Shows/Hides perfect answer
     * @param questionId question that needs to be toggled
     */
    const toggleAnswer = (questionId: number) => {
        setrevealedAnswers(prev => {
            const newSet = new Set(prev);
            if(newSet.has(questionId)){
                newSet.delete(questionId);
            }else{
                newSet.add(questionId);
            }
            return newSet;
        });
    };
    /**
     * set each question's grading to self or AI grading
     * @param questionId question whose mode needs to be changed
     * @param mode self or ai
     */
    const setQuestionGradingMode = (questionId:number, mode:GradingMode) =>{
        setGradingMode(prev => {
            const newMap = new Map(prev);
            newMap.set(questionId,mode);
            return newMap;
        });
    };
    /**
     * Fills user answer in to data structure
     * @param questionId question to update answer
     * @param answer answer that user enters
     */
    const updateUserAnswer = (questionId:number, answer: string) => {
        setUserAnswers(prev => {
            const newMap = new Map(prev);
            newMap.set(questionId, answer);
            return newMap;
        })
    }
    /**
     * AI grades the question with questionID, updates data structure
     * @param questionId question that needs to be graded with AI
     */
    const handleAiGrading = async( questionId:number)=> {
        const userAnswer = userAnswers.get(questionId);

        if(!userAnswer || userAnswer.trim().length === 0){
            setError("Please enter your answer before submitting for grading");
        }

        setGradingQuestionId(questionId);
        console.log(questionId);
        setError('');

        try {
            const res = await getInterviewGrading({
                questionId,userAnswer:userAnswer?.trim()
            });
            console.log(res);
            if(res.data){
                setaiGradings(prev => {
                    const newMap = new Map(prev);
                    newMap.set(questionId,res.data!);
                    return newMap;
                })
            }
        }catch(e){
            setError("failed to grade answer");
        }finally {
            setGradingQuestionId(null);
        }
    };
    /**
     * user self grades the question
     * @param questionId question which user will grade themselves
     * @param grade grade that user sets for their answer
     */
    const handleSelfGrade = ( questionId:number, grade: SelfGrade) => {
        setSelfGrades(prev => {
            const newmap = new Map(prev);
            newmap.set(questionId,grade);
            return newmap;
        });
    };

    /**
     * helper function to effeciently set styling
     * @param questionId style of question
     * @param grade grade given by user
     */
    const getGradeButtonClass = (questionId:number, grade: SelfGrade) =>{
        const currentGrade = selfGrades.get(questionId);
        const baseClass = "px-4 py-2 rounded-lg font-medium transition-all transform hover:scale-105 cursor-pointer";

        if(currentGrade === grade){
            switch (grade) {
                case 'good':
                    return `${baseClass} bg-green-500 text-white shadow-md`
                case 'okay':
                    return `${baseClass} bg-yellow-500 text-white shadow-md`
                case 'bad':
                    return `${baseClass} bg-red-500 text-white shadow-md`
            }
        }

        return `${baseClass} bg-gray-200 text-gray-700 hover: bg-gray-300`;
    };
    /**
     * helper function to style score color
     * @param score value that user got
     */
    const getScoreColor = (score: number)=>{
        if(score >= 8) return 'text-green-600';
        if(score >= 6) return 'text-yellow-600';
        return 'text-red-600';
    }

    return(
        <div className="bg-white dark:bg-gray-800 rounded-xl shadow-xl p-8 mx-auto w-2/3">
            <div className="flex items-center justify-between gap-2 mb-6">
                <h2 className="text-2xl font-semibold text-gray-800 dark:text-white">
                    Interview Preparation
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
                        Generating interview questions...
                    </span>
                </div>
            ) : currentInterview ? (
                <div className="space-y-6">
                    {/* Instructions */}
                    <div className="bg-blue-50 dark:bg-blue-900/20 p-4 rounded-lg border-l-4 border-blue-500">
                        <p className="text-sm text-gray-700 dark:text-gray-300">
                            <strong>How to use:</strong> Practice answering each question. Choose to either self-grade
                            or submit your answer for detailed AI feedback with suggestions for improvement.
                        </p>
                    </div>

                    {/* Interview Questions */}
                    {currentInterview.map((question) => (
                        <div
                            key={question.id}
                            className="bg-gradient-to-r from-purple-50 to-pink-50 dark:from-purple-900/20 dark:to-pink-900/20 p-6 rounded-xl border-l-4 border-purple-500"
                        >
                            {/* Question */}
                            <div className="mb-4">
                                <div className="flex gap-2 text-lg font-semibold text-gray-800 dark:text-white mb-2">
                                    <span>Question {question.id}:</span>
                                </div>
                                <div className="text-gray-700 dark:text-gray-300 ml-6">
                                    <MarkdownRenderer content={question.question} />
                                </div>
                            </div>

                            {/* Grading Mode Selection */}
                            {!gradingMode.has(question.id) && (
                                <div className="flex gap-3 mb-4">
                                    <button
                                        onClick={() => setQuestionGradingMode(question.id, 'self')}
                                        className="flex-1 px-6 py-3 bg-gradient-to-r from-blue-500 to-indigo-600 text-white rounded-lg hover:from-blue-600 hover:to-indigo-700 transition-all transform hover:scale-105 cursor-pointer"
                                    >
                                        Self Grade
                                    </button>
                                    <button
                                        onClick={() => setQuestionGradingMode(question.id, 'ai')}
                                        className="flex-1 px-6 py-3 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105 cursor-pointer"
                                    >
                                        AI Grade My Answer
                                    </button>
                                </div>
                            )}

                            {/* Self-Grading Mode */}
                            {gradingMode.get(question.id) === 'self' && (
                                <div className="space-y-4">
                                    {/* Show Answer Button */}
                                    {!revealedAnswers.has(question.id) && (
                                        <button
                                            onClick={() => toggleAnswer(question.id)}
                                            className="px-6 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 transition-all transform hover:scale-105 cursor-pointer"
                                        >
                                            Show Perfect Answer
                                        </button>
                                    )}

                                    {/* Perfect Answer & Self-Grading */}
                                    {revealedAnswers.has(question.id) && (
                                        <div className="space-y-4 animate-fadeIn">
                                            {/* Perfect Answer */}
                                            <div className="bg-white dark:bg-gray-800 p-4 rounded-lg">
                                                <h4 className="font-semibold text-green-600 dark:text-green-400 mb-2">
                                                    Perfect Answer:
                                                </h4>
                                                <div className="text-gray-700 dark:text-gray-300">
                                                    <MarkdownRenderer content={question.answer} />
                                                </div>
                                            </div>

                                            {/* Self-Grading Buttons */}
                                            <div>
                                                <p className="text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                                    How well did you answer?
                                                </p>
                                                <div className="flex justify-center gap-3">
                                                    <button
                                                        onClick={() => handleSelfGrade(question.id, 'good')}
                                                        className={getGradeButtonClass(question.id, 'good')}
                                                    >
                                                        ✓ Good
                                                    </button>
                                                    <button
                                                        onClick={() => handleSelfGrade(question.id, 'okay')}
                                                        className={getGradeButtonClass(question.id, 'okay')}
                                                    >
                                                        ~ Okay
                                                    </button>
                                                    <button
                                                        onClick={() => handleSelfGrade(question.id, 'bad')}
                                                        className={getGradeButtonClass(question.id, 'bad')}
                                                    >
                                                        ✗ Needs Work
                                                    </button>
                                                </div>
                                            </div>

                                            {/* Hide Answer Button */}
                                            <button
                                                onClick={() => toggleAnswer(question.id)}
                                                className="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 underline cursor-pointer"
                                            >
                                                Hide Answer
                                            </button>
                                        </div>
                                    )}

                                    {/* Change to AI Grading */}
                                    <button
                                        onClick={() => setQuestionGradingMode(question.id, 'ai')}
                                        className="text-sm text-purple-600 dark:text-purple-400 hover:text-purple-800 dark:hover:text-purple-200 underline cursor-pointer"
                                    >
                                        Switch to AI Grading
                                    </button>
                                </div>
                            )}

                            {/* AI Grading Mode */}
                            {gradingMode.get(question.id) === 'ai' && (
                                <div className="space-y-4">
                                    {/* User Answer Input */}
                                    <div>
                                        <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-2">
                                            Your Answer:
                                        </label>
                                        <textarea
                                            value={userAnswers.get(question.id) || ''}
                                            onChange={(e) => updateUserAnswer(question.id, e.target.value)}
                                            placeholder="Type your answer here..."
                                            className="w-full h-32 p-4 border border-gray-300 dark:border-gray-600 rounded-lg resize-none focus:ring-2 focus:ring-purple-500 focus:border-transparent dark:bg-gray-700 dark:text-white"
                                            disabled={aiGradings.has(question.id)}
                                        />
                                    </div>

                                    {/* Submit for Grading Button */}
                                    {!aiGradings.has(question.id) && (
                                        <button
                                            onClick={() => handleAiGrading(question.id)}
                                            disabled={gradingQuestionId === question.id}
                                            className="px-6 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                                        >
                                            {gradingQuestionId === question.id ? (
                                                <span className="flex items-center gap-2">
                                                    <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-white"></div>
                                                    Grading...
                                                </span>
                                            ) : (
                                                'Submit for AI Grading'
                                            )}
                                        </button>
                                    )}

                                    {/* AI Grading Results */}
                                    {aiGradings.has(question.id) && (
                                        <div className="space-y-4 bg-white dark:bg-gray-800 p-6 rounded-lg border-2 border-purple-300 dark:border-purple-700">
                                            {/* Score */}
                                            <div className="flex items-center gap-4 pb-4 border-b border-gray-200 dark:border-gray-700">
                                                <div>
                                                    <p className="text-sm text-gray-600 dark:text-gray-400">Your Score</p>
                                                    <p className={`text-4xl font-bold ${getScoreColor(aiGradings.get(question.id)!.score)}`}>
                                                        {aiGradings.get(question.id)!.score}/10
                                                    </p>
                                                </div>
                                            </div>

                                            {/* Strengths */}
                                            <div>
                                                <h4 className="font-semibold text-green-600 dark:text-green-400 mb-2 flex items-center gap-2">
                                                    <span>✓</span> Strengths
                                                </h4>
                                                <div className="text-gray-700 dark:text-gray-300 ml-6">
                                                    <MarkdownRenderer content={aiGradings.get(question.id)!.strengths} />
                                                </div>
                                            </div>

                                            {/* Feedback */}
                                            <div>
                                                <h4 className="font-semibold text-blue-600 dark:text-blue-400 mb-2">
                                                    Overall Feedback
                                                </h4>
                                                <div className="text-gray-700 dark:text-gray-300 ml-6">
                                                    <MarkdownRenderer content={aiGradings.get(question.id)!.feedback} />
                                                </div>
                                            </div>

                                            {/* Suggestions */}
                                            <div>
                                                <h4 className="font-semibold text-orange-600 dark:text-orange-400 mb-2">
                                                    Suggestions for Improvement
                                                </h4>
                                                <div className="text-gray-700 dark:text-gray-300 ml-6">
                                                    <MarkdownRenderer content={aiGradings.get(question.id)!.suggestions} />
                                                </div>
                                            </div>

                                            {/* Show Perfect Answer */}
                                            {!revealedAnswers.has(question.id) && (
                                                <button
                                                    onClick={() => toggleAnswer(question.id)}
                                                    className="px-4 py-2 bg-gradient-to-r from-green-500 to-emerald-600 text-white rounded-lg hover:from-green-600 hover:to-emerald-700 transition-all transform hover:scale-105 cursor-pointer text-sm"
                                                >
                                                    Show Perfect Answer
                                                </button>
                                            )}

                                            {revealedAnswers.has(question.id) && (
                                                <div className="bg-green-50 dark:bg-green-900/20 p-4 rounded-lg">
                                                    <h4 className="font-semibold text-green-600 dark:text-green-400 mb-2">
                                                        Perfect Answer:
                                                    </h4>
                                                    <div className="text-gray-700 dark:text-gray-300">
                                                        <MarkdownRenderer content={question.answer} />
                                                    </div>
                                                    <button
                                                        onClick={() => toggleAnswer(question.id)}
                                                        className="text-sm text-gray-600 dark:text-gray-400 hover:text-gray-800 dark:hover:text-gray-200 underline cursor-pointer mt-2"
                                                    >
                                                        Hide Perfect Answer
                                                    </button>
                                                </div>
                                            )}

                                            {/* Try Again Button */}
                                            <button
                                                onClick={() => {
                                                    setaiGradings(prev => {
                                                        const newMap = new Map(prev);
                                                        newMap.delete(question.id);
                                                        return newMap;
                                                    });
                                                    setUserAnswers(prev => {
                                                        const newMap = new Map(prev);
                                                        newMap.delete(question.id);
                                                        return newMap;
                                                    });
                                                }}
                                                className="text-sm text-purple-600 dark:text-purple-400 hover:text-purple-800 dark:hover:text-purple-200 underline cursor-pointer"
                                            >
                                                Try Again
                                            </button>
                                        </div>
                                    )}

                                    {/* Change to Self Grading */}
                                    {!aiGradings.has(question.id) && (
                                        <button
                                            onClick={() => setQuestionGradingMode(question.id, 'self')}
                                            className="text-sm text-blue-600 dark:text-blue-400 hover:text-blue-800 dark:hover:text-blue-200 underline cursor-pointer"
                                        >
                                            Switch to Self Grading
                                        </button>
                                    )}
                                </div>
                            )}
                        </div>
                    ))}

                    {/* Summary Stats (for self-grading) */}
                    {selfGrades.size > 0 && (
                        <div className="bg-gray-50 dark:bg-gray-700 p-6 rounded-lg">
                            <h3 className="font-semibold text-gray-800 dark:text-white mb-3">
                                Self-Grading Progress
                            </h3>
                            <div className="flex gap-6">
                                <div className="flex items-center gap-2">
                                    <span className="text-green-500 font-bold text-xl">
                                        {Array.from(selfGrades.values()).filter(g => g === 'good').length}
                                    </span>
                                    <span className="text-gray-600 dark:text-gray-300">Good</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <span className="text-yellow-500 font-bold text-xl">
                                        {Array.from(selfGrades.values()).filter(g => g === 'okay').length}
                                    </span>
                                    <span className="text-gray-600 dark:text-gray-300">Okay</span>
                                </div>
                                <div className="flex items-center gap-2">
                                    <span className="text-red-500 font-bold text-xl">
                                        {Array.from(selfGrades.values()).filter(g => g === 'bad').length}
                                    </span>
                                    <span className="text-gray-600 dark:text-gray-300">Needs Work</span>
                                </div>
                            </div>
                        </div>
                    )}
                </div>
            ) : (
                /* No interview available */
                <div className="text-center py-12 gap-4 flex flex-col items-center justify-center">
                    <p className="text-gray-500 dark:text-gray-400">
                        No interview questions available.
                    </p>
                    <button
                        onClick={handleGenerateInterview}
                        disabled={isLoading}
                        className="px-6 py-2 bg-gradient-to-r from-purple-500 to-pink-600 text-white rounded-lg hover:from-purple-600 hover:to-pink-700 disabled:opacity-50 disabled:cursor-not-allowed transition-all transform hover:scale-105 cursor-pointer"
                    >
                        {isLoading ? 'Processing...' : 'Generate Interview Questions'}
                    </button>
                </div>
            )}
        </div>
    );

}