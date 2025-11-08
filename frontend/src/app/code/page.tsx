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
            <div className="flex items-center justify-between px-1 py-1 bg-[#333333]">
                <span className="text-gray-300 text-sm font-bold flex items-center py-1 px-2 rounded hover:bg-[#434343] cursor-pointer">
                    <LuBookOpenText className="text-blue-500 mr-1" /> Description
                </span>
            </div>
            <div className="p-6">
              <h1 className="text-2xl font-bold mb-4 bg-[#333333]">
                Problem Description
              </h1>
              <p className="text-gray-700">
                Write your problem description here...
              </p>
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