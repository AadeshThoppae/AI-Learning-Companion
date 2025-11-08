"use client";

import { useState, useEffect, useRef } from "react";
import Split from "@uiw/react-split";
import Editor from "@monaco-editor/react";
import type { Monaco } from "@monaco-editor/react";
import type { editor } from "monaco-editor";
import { leetcodeTheme } from "@/config/monaco/javaTheme";
import { javaTokenizer } from "@/config/monaco/javaTokenizer";
import { javaFormattingProvider } from "@/config/monaco/javaFormatter";
import { FaCode, FaPen, FaRegSquareCheck, FaPersonPraying, FaPlay } from "react-icons/fa6";
import { MdFormatIndentIncrease, MdOutlineCloudUpload } from "react-icons/md";
import { RiResetLeftFill } from "react-icons/ri";
import { IoResizeOutline } from "react-icons/io5";
import { LuBookOpenText } from "react-icons/lu";
import VerticalSplitter from "@/components/VerticalSplitter";
import Link from "next/link";
import { getCodingQuestion } from "@/services/codingService";
import { CodingQuestion, Difficulty } from "@/types/codingTypes";
import MarkdownRenderer from "@/components/MarkdownRenderer";


export default function CodePage() {
  const [code, setCode] = useState(`import java.util.*;

class Solution {
    public int solution(int[] nums) {
        // Write your code here

        return 0;
    }
}`);
  const [autocompleteEnabled] = useState(false);
  const [editorInstance, setEditorInstance] =
    useState<editor.IStandaloneCodeEditor | null>(null);
  const [selectedPanel, setSelectedPanel] = useState<
    "description" | "editor" | "console"
  >("description");
  const editorContainerRef = useRef<HTMLDivElement>(null);
  const [codingQuestion, setCodingQuestion] = useState<CodingQuestion | null>(null);
  const [loading, setLoading] = useState(true);

  const handleEditorWillMount = (monaco: Monaco) => {
    monaco.editor.defineTheme("leetcode", leetcodeTheme);
    monaco.languages.setMonarchTokensProvider("java", javaTokenizer);
    monaco.languages.registerDocumentFormattingEditProvider(
      "java",
      javaFormattingProvider
    );
  };

  const handleEditorDidMount = (editor: editor.IStandaloneCodeEditor) => {
    setEditorInstance(editor);
    setTimeout(() => editor.layout(), 0);
  };

  const formatCode = () => {
    editorInstance?.getAction("editor.action.formatDocument")?.run();
  };

  useEffect(() => {
    if (!editorInstance || !editorContainerRef.current) return;

    editorInstance.layout();
    const observer = new ResizeObserver(() => editorInstance.layout());
    observer.observe(editorContainerRef.current);
    window.addEventListener("resize", () => editorInstance.layout());
    return () => observer.disconnect();
  }, [editorInstance]);

  const dummydata: CodingQuestion = {
    "id": "java_interface_easy_001",
    "topicId": 1,
    "title": "Simulate Diverse Entity Actions",
    "description": "In object-oriented programming, interfaces define a contract for classes. Any class that implements an interface must provide an implementation for all methods declared in that interface. This allows you to treat objects of different types uniformly, as long as they share a common interface.\n\nYour task is to:\n1.  **Define an interface** named `LoggableAction` with a single method: `String performAction()`. This method should return a String describing the action performed.\n2.  **Implement this interface** in two concrete classes:\n    *   `Robot`: Its `performAction()` method should return the string \"Robot performing automated task.\".\n    *   `Human`: Its `performAction()` method should return the string \"Human performing daily task.\".\n3.  **Complete the `simulateActions` method** in the `Solution` class. This method will receive an array of `String`s, where each string represents an entity type (\"Robot\" or \"Human\"). For each entity type in the input array, you should:\n    *   Create an instance of the corresponding class (`Robot` or `Human`).\n    *   Call its `performAction()` method.\n    *   Collect all the returned action strings into a `List<String>` and return it.",
    "difficulty": "EASY",
    "examples": [
      {
        "input": "[\"Robot\", \"Human\"]",
        "output": "[\"Robot performing automated task.\", \"Human performing daily task.\"]",
        "explanation": "A 'Robot' object is created and its action is logged. Then a 'Human' object is created and its action is logged. The results are collected in order."
      },
      {
        "input": "[\"Human\", \"Human\", \"Robot\"]",
        "output": "[\"Human performing daily task.\", \"Human performing daily task.\", \"Robot performing automated task.\"]",
        "explanation": "Two 'Human' objects are created, followed by one 'Robot' object, and their respective actions are logged in the order they appear in the input array."
      }
    ],
    "methodSignature": "public static java.util.List<String> simulateActions(String[] entityTypes)",
    "starterCode": "import java.util.ArrayList;\nimport java.util.List;\n\n// TODO: Define the 'LoggableAction' interface here.\n// It should declare a single method: String performAction();\n\n// TODO: Implement the 'LoggableAction' interface in a class named 'Robot'.\n// Its performAction() method should return \"Robot performing automated task.\".\n\n// TODO: Implement the 'LoggableAction' interface in a class named 'Human'.\n// Its performAction() method should return \"Human performing daily task.\".\n\n\npublic class Solution {\n    public static List<String> simulateActions(String[] entityTypes) {\n        List<String> results = new ArrayList<>();\n\n        // TODO: Iterate through the 'entityTypes' array.\n        // For each string 'type' in 'entityTypes':\n        //   1. Based on the 'type' string (\"Robot\" or \"Human\"), create an instance of the\n        //      corresponding class.\n        //   2. Use polymorphism: assign the created instance to a variable of type 'LoggableAction'.\n        //   3. Call the 'performAction()' method on this 'LoggableAction' variable.\n        //   4. Add the returned string to the 'results' list.\n\n        return results;\n    }\n}",
    "testCases": [
      {
        "id": 1,
        "input": "[\"Robot\", \"Human\"]",
        "expectedOutput": "[\"Robot performing automated task.\", \"Human performing daily task.\"]",
        "hidden": false
      },
      {
        "id": 2,
        "input": "[\"Human\", \"Human\", \"Robot\"]",
        "expectedOutput": "[\"Human performing daily task.\", \"Human performing daily task.\", \"Robot performing automated task.\"]",
        "hidden": false
      },
      {
        "id": 3,
        "input": "[\"Robot\"]",
        "expectedOutput": "[\"Robot performing automated task.\"]",
        "hidden": true
      },
      {
        "id": 4,
        "input": "[]",
        "expectedOutput": "[]",
        "hidden": true
      },
      {
        "id": 5,
        "input": "[\"Robot\", \"Robot\", \"Human\", \"Human\", \"Robot\"]",
        "expectedOutput": "[\"Robot performing automated task.\", \"Robot performing automated task.\", \"Human performing daily task.\", \"Human performing daily task.\", \"Robot performing automated task.\"]",
        "hidden": true
      }
    ],
    "hints": "Remember that an interface defines a contract, and concrete classes must implement all its methods.\nUse a `for` loop to iterate through the `entityTypes` array. Inside the loop, use `if-else if` statements to determine which class to instantiate.\nYou can assign an instance of an implementing class (e.g., `Robot` or `Human`) to a variable of the interface type (`LoggableAction`). This demonstrates polymorphism.",
    "constraints": {
      "timeComplexity": "O(N)",
      "spaceComplexity": "O(N)",
      "rules": [
        "0 <= entityTypes.length <= 100",
        "Each string in `entityTypes` will be exactly \"Robot\" or \"Human\"."
      ]
    }
  };

  // Fetch coding question on page load
  useEffect(() => {
    const fetchCodingQuestion = async () => {
      try {
        setLoading(true);
        // TODO: Get topicId and difficulty from query params or props
        const dummyRequest = {
          topicId: 1,
          difficulty: "EASY" as Difficulty,
        };

        /* const response = await getCodingQuestion(dummyRequest);
        if (response.data) {
          setCodingQuestion(response.data);
          setCode(response.data.starterCode);
        } */
        // Using dummy data for now
        setCodingQuestion(dummydata);
        setCode(dummydata.starterCode);
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
                  <button className="bg-[#262626] px-2 h-full text-gray-400 text-sm rounded-s hover:bg-[#333333] cursor-pointer">
                  <FaPlay/>
                  </button>
                  <button className="flex items-center px-2 h-full bg-[#262626] text-green-500 text-sm font-bold rounded-e hover:bg-[#333333] cursor-pointer">
                  <MdOutlineCloudUpload className="mr-2 text-base"/> Submit
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
          {/* Left panel */}
          <div
            onClick={() => setSelectedPanel("description")}
            className={`bg-[#262626] overflow-auto rounded ${
              selectedPanel === "description"
                ? "border border-[#606060]"
                : "border border-transparent"
            }`}
            style={{ width: "25%" }}
          >
            <div className="sticky top-0 z-10 flex items-center justify-between px-1 py-1 bg-[#333333]">
                <span className="text-gray-300 text-sm font-bold flex items-center py-1 px-2 rounded hover:bg-[#434343] cursor-pointer">
                    <LuBookOpenText className="text-blue-500 mr-1" /> Description
                </span>
            </div>
            <div className="p-6">
              {loading ? (
                <div className="text-gray-400">Loading question...</div>
              ) : codingQuestion ? (
                <>
                  <h1 className="text-2xl font-bold mb-2 text-white">
                    {codingQuestion.title}
                  </h1>
                  <span className={`inline-block px-2 py-1 rounded text-xs font-semibold mb-4 ${
                    codingQuestion.difficulty === "EASY" ? 'bg-green-600 text-white' :
                    codingQuestion.difficulty === "MEDIUM" ? 'bg-yellow-600 text-white' :
                    'bg-red-600 text-white'
                  }`}>
                    {codingQuestion.difficulty}
                  </span>
                  <p className="text-gray-300 mb-4 whitespace-pre-wrap">
                    <MarkdownRenderer content={codingQuestion.description}/>
                  </p>
                  {codingQuestion.examples && codingQuestion.examples.length > 0 && (
                    <div className="mb-4">
                      <h3 className="font-bold text-white mb-2">Examples:</h3>
                      {codingQuestion.examples.map((example, idx) => (
                        <div key={idx} className="mb-3">
                          <div className="bg-[#1e1e1e] rounded-lg overflow-hidden border border-[#444444]">
                            <div className="px-3 py-2 bg-[#2a2a2a] border-b border-[#444444]">
                              <span className="text-gray-400 text-xs font-semibold">Example {idx + 1}</span>
                            </div>
                            <div className="p-3 space-y-2">
                              <div>
                                <div className="text-gray-400 text-xs font-semibold mb-1">Input:</div>
                                <code className="block bg-[#262626] text-green-400 px-3 py-2 rounded font-mono text-sm border border-[#333333]">
                                  {example.input}
                                </code>
                              </div>
                              <div>
                                <div className="text-gray-400 text-xs font-semibold mb-1">Output:</div>
                                <code className="block bg-[#262626] text-blue-400 px-3 py-2 rounded font-mono text-sm border border-[#333333]">
                                  {example.output}
                                </code>
                              </div>
                              {example.explanation && (
                                <div className="pt-1">
                                  <div className="text-gray-400 text-xs font-semibold mb-1">Explanation:</div>
                                  <p className="text-gray-300 text-sm italic">{example.explanation}</p>
                                </div>
                              )}
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                  {codingQuestion.constraints && (
                    <div className="mb-4">
                      <h3 className="font-bold text-white mb-2">Constraints:</h3>
                      {(codingQuestion.constraints.timeComplexity || codingQuestion.constraints.spaceComplexity) && (
                        <div className="mb-2 space-y-1">
                          {codingQuestion.constraints.timeComplexity && (
                            <p className="text-gray-300">
                              <strong>Time Complexity:</strong> <code className="bg-[#333333] text-green-400 px-1.5 py-0.5 rounded font-mono text-sm">{codingQuestion.constraints.timeComplexity}</code>
                            </p>
                          )}
                          {codingQuestion.constraints.spaceComplexity && (
                            <p className="text-gray-300">
                              <strong>Space Complexity:</strong> <code className="bg-[#333333] text-green-400 px-1.5 py-0.5 rounded font-mono text-sm">{codingQuestion.constraints.spaceComplexity}</code>
                            </p>
                          )}
                        </div>
                      )}
                      <ul className="list-disc list-inside text-gray-300">
                        {codingQuestion.constraints.rules
                          .filter(rule => rule && rule.trim())
                          .map((rule, idx) => (
                          <li key={idx} className="text-gray-300">
                            <MarkdownRenderer content={rule} inline />
                          </li>
                        ))}
                      </ul>
                    </div>
                  )}
                </>
              ) : (
                <p className="text-gray-400">Failed to load question</p>
              )}
            </div>
          </div>

          {/* Right panel */}
          <div className="flex-1 flex flex-col min-h-0">
            <VerticalSplitter
              defaultTopHeight={60}
              minTopHeight={20}
              minBottomHeight={15}
              onResize={handleSplitChange}
              topContent={
                <div
                  onClick={() => setSelectedPanel("editor")}
                  className={`flex flex-col h-full bg-[#333333] rounded ${
                    selectedPanel === "editor"
                      ? "border border-[#606060]"
                      : "border border-transparent"
                  }`}
                >
                  <div className="flex items-center px-1 py-1">
                    <span className="text-gray-300 text-sm font-bold flex items-center py-1 px-2 rounded hover:bg-[#434343] cursor-pointer">
                      <FaCode className="text-green-500 mr-1" /> Code
                    </span>
                  </div>

                  <div className="flex items-center justify-between px-1 py-1 text-xs bg-[#262626] border-b border-[#444444]">
                    <div className="flex">
                        <span className="text-[#B1B1B1] font-bold px-2 cursor-pointer hover:bg-[#333333] rounded flex items-center">
                        Java
                        </span>
                        <span className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded">
                        <FaPen className="mr-1 text-[10px]" /> Autocomplete
                        </span>
                    </div>
                    <div className="flex">
                        <button type="button" onClick={formatCode} className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded">
                        <MdFormatIndentIncrease className="text-sm my-[1px]"  />
                        </button>
                        <span className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded">
                        <RiResetLeftFill className="text-sm my-[1px]"  />
                        </span>
                        <span className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded">
                        <IoResizeOutline className="text-sm my-[1px]"  />
                        </span>
                    </div>
                  </div>

                  <div
                    ref={editorContainerRef}
                    className="flex-1 min-h-0 no-monaco-sticky"
                  >
                    <Editor
                      height="100%"
                      defaultLanguage="java"
                      value={code}
                      onChange={(v) => setCode(v || "")}
                      theme="leetcode"
                      beforeMount={handleEditorWillMount}
                      onMount={handleEditorDidMount}
                      options={{
                        minimap: { enabled: false },
                        fontSize: 14,
                        lineNumbers: "on",
                        scrollBeyondLastLine: false,
                        automaticLayout: false,
                        formatOnPaste: true,
                        scrollbar: { useShadows: false },
                      }}
                    />
                  </div>
                </div>
              }
              bottomContent={
                <div
                  onClick={() => setSelectedPanel("console")}
                  className={`bg-[#262626] rounded flex flex-col h-full ${
                    selectedPanel === "console"
                      ? "border border-[#606060]"
                      : "border border-transparent"
                  }`}
                >
                  <div className="flex items-center px-1 py-1 bg-[#333333]">
                    <span className="text-gray-300 text-sm font-bold flex items-center py-1 px-2 rounded hover:bg-[#434343] cursor-pointer">
                      <FaRegSquareCheck className="text-green-500 mr-1"/> Testcase
                    </span>
                    <span className="w-[1px] bg-[#606060] h-3" />
                    <span className="text-gray-300 text-sm font-bold flex items-center py-1 px-2 rounded hover:bg-[#434343] cursor-pointer">
                      <FaPersonPraying className="text-green-500 mr-1"/> Test Result
                    </span>
                  </div>
                  <div className="p-4 font-mono text-sm text-green-400 flex-1 overflow-auto">
                    <div>$ Ready to run your code...</div>
                  </div>
                </div>
              }
            />
          </div>
        </Split>
      </div>
    </div>
  );
}