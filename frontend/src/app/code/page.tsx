"use client";

import { useState, useEffect, useRef } from "react";
import Split from "@uiw/react-split";
import type { Monaco } from "@monaco-editor/react";
import type { editor } from "monaco-editor";
import { leetcodeTheme } from "@/config/monaco/javaTheme";
import { javaTokenizer } from "@/config/monaco/javaTokenizer";
import { javaFormattingProvider } from "@/config/monaco/javaFormatter";
import { FaPlay } from "react-icons/fa6";
import { MdOutlineCloudUpload } from "react-icons/md";
import VerticalSplitter from "@/components/VerticalSplitter";
import Link from "next/link";
import { getCodingQuestion, testCode, submitCode } from "@/services/codingService";
import { CodingQuestion, Difficulty, ExecutionResult } from "@/types/codingTypes";
import DescriptionPanel from "@/components/code/DescriptionPanel";
import CodeEditorPanel from "@/components/code/CodeEditorPanel";
import ConsolePanel from "@/components/code/ConsolePanel";

export default function CodePage() {
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
				// TODO: Get topicId and difficulty from query params or props
				const dummyRequest = {
					topicId: 3,
					difficulty: "EASY" as Difficulty,
				};

				const response = await getCodingQuestion(dummyRequest);
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
	}, []);

	const handleSplitChange = () => {
		if (editorInstance) setTimeout(() => editorInstance.layout(), 0);
	};

	return (
		<div className="h-screen p-2 flex flex-col">
			<div className="h-9 px-4 flex items-center pb-2">
				<div className="flex-1">
					<Link href={"/"} className="text-gray-300 text-lg font-bold">
						AI Learning Companion - Code Editor
					</Link>
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
				<div className="flex-1"></div>
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
