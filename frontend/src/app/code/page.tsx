"use client";

import { useState } from 'react';
import Split from '@uiw/react-split';
import Editor from '@monaco-editor/react';
import type { Monaco } from '@monaco-editor/react';
import { leetcodeTheme } from '@/config/monaco/javaTheme';
import { javaTokenizer } from '@/config/monaco/javaTokenizer';

export default function CodePage() {
    const [code, setCode] = useState(`import java.util.*;

class Solution {
    public int solution(int[] nums) {
        // Write your code here

        return 0;
    }
}`);
    const [autocompleteEnabled, setAutocompleteEnabled] = useState(false);

    const handleEditorWillMount = (monaco: Monaco) => {
        // Define custom LeetCode-style theme
        monaco.editor.defineTheme('leetcode', leetcodeTheme);

        // Set custom tokenizer for better syntax highlighting
        monaco.languages.setMonarchTokensProvider('java', javaTokenizer);
    };
    return (
        <div className="h-screen bg-gray-900">
            <Split
                style={{ height: '100%' }}
            >
                {/* Left Panel - Description (25% default width) */}
                <div
                    className="bg-white overflow-auto"
                    style={{ width: '25%' }}
                >
                    <div className="p-6">
                        <h1 className="text-2xl font-bold mb-4">Problem Description</h1>
                        <div className="space-y-4">
                            <p className="text-gray-700">
                                Write your problem description here...
                            </p>
                            <div>
                                <h2 className="text-lg font-semibold mb-2">Example:</h2>
                                <div className="bg-gray-50 p-4 rounded border border-gray-200 font-mono text-sm">
                                    <div>Input: nums = [1, 2, 3]</div>
                                    <div>Output: 6</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                {/* Right Side - Code Editor + Terminal */}
                <div className="flex-1" style={{ width: '75%' }}>
                    <Split mode="vertical">
                        {/* Top Panel - Code Editor */}
                        <div
                            className="flex flex-col bg-gray-900"
                            style={{ height: '60%' }}
                        >
                            <div className="flex items-center justify-between px-4 py-2 border-b border-gray-700">
                                <div className="flex items-center gap-4">
                                    <span className="text-gray-300 text-sm font-medium">Code Editor</span>
                                    <label className="flex items-center gap-2 cursor-pointer">
                                        <input
                                            type="checkbox"
                                            checked={autocompleteEnabled}
                                            onChange={(e) => setAutocompleteEnabled(e.target.checked)}
                                            className="w-4 h-4 cursor-pointer"
                                        />
                                        <span className="text-gray-400 text-xs">Autocomplete</span>
                                    </label>
                                </div>
                                <div className="flex items-center gap-2">
                                    <button className="px-3 py-1 bg-green-600 hover:bg-green-700 text-white rounded text-sm">
                                        Run Code
                                    </button>
                                    <button className="px-3 py-1 bg-blue-600 hover:bg-blue-700 text-white rounded text-sm">
                                        Submit
                                    </button>
                                </div>
                            </div>
                            <div className="flex-1">
                                <Editor
                                    height="100%"
                                    defaultLanguage="java"
                                    value={code}
                                    onChange={(value) => setCode(value || '')}
                                    theme="leetcode"
                                    beforeMount={handleEditorWillMount}
                                    options={{
                                        minimap: { enabled: false },
                                        fontSize: 14,
                                        lineNumbers: 'on',
                                        scrollBeyondLastLine: false,
                                        automaticLayout: true,
                                        suggestOnTriggerCharacters: autocompleteEnabled,
                                        quickSuggestions: autocompleteEnabled,
                                        wordBasedSuggestions: autocompleteEnabled ? 'allDocuments' : 'off',
                                        tabCompletion: autocompleteEnabled ? 'on' : 'off',
                                        acceptSuggestionOnEnter: autocompleteEnabled ? 'on' : 'off',
                                    }}
                                />
                            </div>
                        </div>

                        {/* Bottom Panel - Terminal */}
                        <div
                            className="bg-gray-950 overflow-auto"
                            style={{ height: '40%' }}
                        >
                            <div className="px-4 py-2 bg-gray-900 border-b border-gray-700">
                                <span className="text-gray-300 text-sm font-medium">Console</span>
                            </div>
                            <div className="p-4 font-mono text-sm text-green-400">
                                <div>$ Ready to run your code...</div>
                            </div>
                        </div>
                    </Split>
                </div>
            </Split>
        </div>
    );
}