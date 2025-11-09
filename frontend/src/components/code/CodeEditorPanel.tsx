"use client";

import { RefObject } from "react";
import Editor from "@monaco-editor/react";
import type { Monaco } from "@monaco-editor/react";
import type { editor } from "monaco-editor";
import { FaCode, FaPen } from "react-icons/fa6";
import { MdFormatIndentIncrease } from "react-icons/md";
import { RiResetLeftFill } from "react-icons/ri";
import { IoResizeOutline } from "react-icons/io5";

interface CodeEditorPanelProps {
  code: string;
  setCode: (code: string) => void;
  isSelected: boolean;
  onSelect: () => void;
  editorContainerRef: RefObject<HTMLDivElement | null>;
  handleEditorWillMount: (monaco: Monaco) => void;
  handleEditorDidMount: (editor: editor.IStandaloneCodeEditor) => void;
  formatCode: () => void;
}

export default function CodeEditorPanel({
  code,
  setCode,
  isSelected,
  onSelect,
  editorContainerRef,
  handleEditorWillMount,
  handleEditorDidMount,
  formatCode,
}: CodeEditorPanelProps) {
  return (
    <div
      onClick={onSelect}
      className={`flex flex-col h-full bg-[#333333] rounded ${
        isSelected ? "border border-[#606060]" : "border border-transparent"
      }`}
    >
      <div className="flex items-center px-1 py-1">
        <span className="text-gray-300 text-sm font-bold flex items-center py-1 px-2 rounded hover:bg-[#434343] cursor-pointer">
          <FaCode className="text-blue-500 mr-1" /> Code
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
          <button
            type="button"
            onClick={formatCode}
            className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded"
          >
            <MdFormatIndentIncrease className="text-sm my-[1px]" />
          </button>
          <span className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded">
            <RiResetLeftFill className="text-sm my-[1px]" />
          </span>
          <span className="text-[#B1B1B1] font-bold px-2 cursor-pointer flex items-center hover:bg-[#333333] rounded">
            <IoResizeOutline className="text-sm my-[1px]" />
          </span>
        </div>
      </div>

      <div ref={editorContainerRef} className="flex-1 min-h-0 no-monaco-sticky">
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
            fontSize: 12,
            lineNumbers: "on",
            scrollBeyondLastLine: false,
            automaticLayout: false,
            formatOnPaste: true,
            scrollbar: { useShadows: false },
          }}
        />
      </div>
    </div>
  );
}
