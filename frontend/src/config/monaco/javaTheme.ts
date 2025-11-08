import type { editor } from "monaco-editor";

export const leetcodeTheme: editor.IStandaloneThemeData = {
	base: "vs-dark",
	inherit: true,
	rules: [
		// Control flow keywords (pink) - for, if, while, return, new, etc.
		{ token: "keyword.control", foreground: "C586C0" },

		// Declaration keywords (blue) - class, public, private, static, etc.
		{ token: "keyword.declaration", foreground: "569CD6" },

		// Primitive types (green) - int, long, double, void, etc.
		{ token: "keyword.type", foreground: "4EC9B0" },

		// Other keywords
		{ token: "keyword", foreground: "569CD6" },

		// Types and classes (green) - HashMap, Integer, Solution, etc.
		{ token: "type", foreground: "4EC9B0" },
		{ token: "type.identifier", foreground: "4EC9B0" },
		{ token: "type.predefined", foreground: "4EC9B0" },

		// Method names (yellow)
		{ token: "identifier.method", foreground: "DCDCAA" },

		// Variables (light blue)
		{ token: "identifier", foreground: "9CDCFE" },
		{ token: "variable", foreground: "9CDCFE" },

		// Strings, numbers, comments
		{ token: "string", foreground: "CE9178" },
		{ token: "number", foreground: "B5CEA8" },
		{ token: "comment", foreground: "6A9955" },

		// Delimiters and operators
		{ token: "delimiter", foreground: "D4D4D4" },
		{ token: "operator", foreground: "D4D4D4" },
	],
	colors: {
		"editor.background": "#1E1E1E",
		"editor.foreground": "#D4D4D4",
		"editor.lineHighlightBackground": "#282828",
		"editor.selectionBackground": "#264F78",
	},
};
