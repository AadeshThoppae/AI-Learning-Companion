"use client";

import { useState, useEffect, useRef, use } from "react";
import Split from "@uiw/react-split";
import type { Monaco } from "@monaco-editor/react";
import type { editor } from "monaco-editor";
import { leetcodeTheme } from "@/config/monaco/javaTheme";
import { javaTokenizer } from "@/config/monaco/javaTokenizer";
import { javaFormattingProvider } from "@/config/monaco/javaFormatter";
import { FaPlay } from "react-icons/fa6";
import { MdOutlineCloudUpload } from "react-icons/md";
import { IoMdArrowBack } from "react-icons/io";
import { VscRefresh } from "react-icons/vsc";
import VerticalSplitter from "@/components/VerticalSplitter";
import Link from "next/link";
import { getCodingQuestion, testCode, submitCode } from "@/services/codingService";
import { CodingQuestion, ExecutionResult } from "@/types/codingTypes";
import DescriptionPanel from "@/components/code/DescriptionPanel";
import CodeEditorPanel from "@/components/code/CodeEditorPanel";
import ConsolePanel from "@/components/code/ConsolePanel";

export default function CodePage({ params }: { params: Promise<{ topicId: string }> }) {
	const { topicId: topicIdString } = use(params);
	const topicId = parseInt(topicIdString, 10);

	const [code, setCode] = useState(`import java.util.*;

class Solution {
    public int solution(int[] nums) {
        // Write your code here

        return 0;
    }
}`);
	const [autocompleteEnabled] = useState(false);
	const [editorInstance, setEditorInstance] = useState<editor.IStandaloneCodeEditor | null>(null);
	const [selectedPanel, setSelectedPanel] = useState<"description" | "editor" | "console">("description");
	const editorContainerRef = useRef<HTMLDivElement>(null);
	const [codingQuestion, setCodingQuestion] = useState<CodingQuestion | null>(null);
	const [loading, setLoading] = useState(true);
	const [executionResult, setExecutionResult] = useState<ExecutionResult | null>(null);
	const [testing, setTesting] = useState(false);
	const [submitting, setSubmitting] = useState(false);
	const [regenerating, setRegenerating] = useState(false);

	const handleEditorWillMount = (monaco: Monaco) => {
		monaco.editor.defineTheme("leetcode", leetcodeTheme);
		monaco.languages.setMonarchTokensProvider("java", javaTokenizer);
		monaco.languages.registerDocumentFormattingEditProvider("java", javaFormattingProvider);
	};

	const handleEditorDidMount = (editor: editor.IStandaloneCodeEditor) => {
		setEditorInstance(editor);
		setTimeout(() => editor.layout(), 0);
	};

	const formatCode = () => {
		editorInstance?.getAction("editor.action.formatDocument")?.run();
	};

	const handleRunCode = async () => {
		if (!codingQuestion) {
			console.error("No coding question available");
			return;
		}

		try {
			setTesting(true);
			setSelectedPanel("console"); // Switch to console panel
			const response = await testCode({
				questionId: codingQuestion.id,
				code,
			});

			if (response.data) {
				setExecutionResult(response.data);
			}
		} catch (error) {
			console.error("Failed to test code:", error);
			// Set an error state in execution result
			setExecutionResult({
				success: false,
				passedTests: 0,
				totalTests: 0,
				results: [],
				error: error instanceof Error ? error.message : "Failed to test code",
				executionTime: 0,
			});
		} finally {
			setTesting(false);
		}
	};

	const handleSubmit = async () => {
		if (!codingQuestion) {
			console.error("No coding question available");
			return;
		}

		try {
			setSubmitting(true);
			setSelectedPanel("console"); // Switch to console panel
			const response = await submitCode({
				questionId: codingQuestion.id,
				code,
			});

			if (response.data) {
				setExecutionResult(response.data);
			}
		} catch (error) {
			console.error("Failed to submit code:", error);
			// Set an error state in execution result
			setExecutionResult({
				success: false,
				passedTests: 0,
				totalTests: 0,
				results: [],
				error: error instanceof Error ? error.message : "Failed to submit code",
				executionTime: 0,
			});
		} finally {
			setSubmitting(false);
		}
	};

	const handleRegenerate = async () => {
		try {
			setRegenerating(true);
			setExecutionResult(null); // Clear previous results

			const response = await getCodingQuestion({
				topicId: topicId,
				regenerate: true,
			});

			if (response.data) {
				setCodingQuestion(response.data);
				setCode(response.data.starterCode);
			}
		} catch (error) {
			console.error("Failed to regenerate coding question:", error);
		} finally {
			setRegenerating(false);
		}
	};

	useEffect(() => {
		if (!editorInstance || !editorContainerRef.current) return;

		editorInstance.layout();
		const observer = new ResizeObserver(() => editorInstance.layout());
		observer.observe(editorContainerRef.current);
		window.addEventListener("resize", () => editorInstance.layout());
		return () => observer.disconnect();
	}, [editorInstance]);

	// Fetch coding question on page load
	useEffect(() => {
		const fetchCodingQuestion = async () => {
			try {
				setLoading(true);

				const response = await getCodingQuestion({
					topicId: topicId,
				});

				if (response.data) {
					setCodingQuestion(response.data);
					setCode(response.data.starterCode);
				}
			} catch (error) {
				console.error("Failed to fetch coding question:", error);
			} finally {
				setLoading(false);
			}
		};

		fetchCodingQuestion();
	}, [topicId]);

	const handleSplitChange = () => {
		if (editorInstance) setTimeout(() => editorInstance.layout(), 0);
	};

	const getDifficultyColor = (difficulty: string) => {
		switch (difficulty) {
			case "EASY":
				return "bg-green-500/20 text-green-400";
			case "MEDIUM":
				return "bg-yellow-500/20 text-yellow-400";
			case "HARD":
				return "bg-red-500/20 text-red-400";
			default:
				return "bg-gray-500/20 text-gray-400";
		}
	};

	return (
		<div className="h-screen p-2 flex flex-col">
			<div className="h-9 px-4 flex items-center pb-2">
			<div className="flex-1 flex items-center gap-3">
				<Link
					href={"/code"}
					className="text-gray-400 hover:text-gray-300 transition-colors flex items-center"
				>
					<IoMdArrowBack className="text-xl" />
				</Link>
				<div className="flex items-center gap-2">
					<span className="text-gray-300 text-lg font-bold">
						{codingQuestion ? codingQuestion.title : "Loading..."}
					</span>
					{codingQuestion && (
						<span className={`px-2 py-1 text-xs font-semibold rounded ${getDifficultyColor(codingQuestion.difficulty)}`}>
							{codingQuestion.difficulty}
						</span>
					)}
				</div>
			</div>
				<div className="flex-1 flex justify-center items-center">
					<div className="items-center flex h-7 gap-[1px]">
						<button
							onClick={handleRunCode}
							disabled={testing || !codingQuestion}
							className="bg-[#262626] px-2 h-full text-gray-400 text-sm rounded-s hover:bg-[#333333] cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
						>
							{testing ? "Running..." : <FaPlay />}
						</button>
					<button
						onClick={handleSubmit}
						disabled={submitting || testing || !codingQuestion}
						className="flex items-center px-2 h-full bg-[#262626] text-blue-500 text-sm font-bold rounded-e hover:bg-[#333333] cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
					>
						{submitting ? (
							"Submitting..."
						) : (
							<>
								<MdOutlineCloudUpload className="mr-2 text-base" /> Submit
							</>
						)}
					</button>
					</div>
				</div>
				<div className="flex-1 flex justify-end items-center">
					<button
						onClick={handleRegenerate}
						disabled={regenerating || loading || testing || submitting}
						className="flex items-center gap-2 px-3 py-1 text-sm bg-gray-700 text-gray-300 rounded hover:bg-gray-600 transition-colors disabled:opacity-50 disabled:cursor-not-allowed"
						title="Generate a new question for this topic"
					>
						<VscRefresh className={regenerating ? "animate-spin" : ""} />
						{regenerating ? "Regenerating..." : "Regenerate"}
					</button>
				</div>
			</div>
			<div className="flex-1 min-h-0">
				<Split
					onChange={handleSplitChange}
					style={{ height: "100%" }}
					renderBar={({ onMouseDown, ...props }) => (
						<div
							{...props}
							className="w-1.5 flex flex-col bg-transparent select-none justify-center relative"
						>
							<span className="mt-[-1px] w-[2px] h-5 block bg-[#ffffff24] self-center" />
							<div
								onMouseDown={onMouseDown}
								className="absolute inset-0 hover:bg-[#1990ff] w-0.5 mx-0.5 cursor-ew-resize"
							/>
						</div>
					)}
				>
					{/* Left panel - Description */}
					<DescriptionPanel
						codingQuestion={codingQuestion}
						loading={loading}
						isSelected={selectedPanel === "description"}
						onSelect={() => setSelectedPanel("description")}
					/>

					{/* Right panel */}
					<div className="flex-1 flex flex-col min-h-0">
						<VerticalSplitter
							defaultTopHeight={60}
							minTopHeight={20}
							minBottomHeight={15}
							onResize={handleSplitChange}
							topContent={
								<CodeEditorPanel
									code={code}
									setCode={setCode}
									isSelected={selectedPanel === "editor"}
									onSelect={() => setSelectedPanel("editor")}
									editorContainerRef={editorContainerRef}
									handleEditorWillMount={handleEditorWillMount}
									handleEditorDidMount={handleEditorDidMount}
									formatCode={formatCode}
								/>
							}
							bottomContent={
								<ConsolePanel
									isSelected={selectedPanel === "console"}
									onSelect={() => setSelectedPanel("console")}
									testCases={codingQuestion?.testCases}
									executionResult={executionResult}
									testing={testing}
								/>
							}
						/>
					</div>
				</Split>
			</div>
		</div>
	);
}
