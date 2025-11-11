import type { languages } from "monaco-editor";

export function formatJavaCode(code: string): string {
	let formatted = code;

	// Remove trailing whitespace from each line
	formatted = formatted
		.split("\n")
		.map((line) => line.trimEnd())
		.join("\n");

	// Remove multiple consecutive blank lines (max 1 blank line)
	formatted = formatted.replace(/\n\n\n+/g, "\n\n");

	// Format the code with proper indentation
	const lines = formatted.split("\n");
	let indentLevel = 0;
	const indentSize = 4;
	const formattedLines: string[] = [];

	for (let i = 0; i < lines.length; i++) {
		const line = lines[i].trim();

		// Skip empty lines
		if (line === "") {
			formattedLines.push("");
			continue;
		}

		// Decrease indent for closing braces
		if (line.startsWith("}")) {
			indentLevel = Math.max(0, indentLevel - 1);
		}

		// Add the line with proper indentation
		const indent = " ".repeat(indentLevel * indentSize);
		formattedLines.push(indent + line);

		// Increase indent after opening braces
		if (line.endsWith("{")) {
			indentLevel++;
		}

		// Handle closing brace on same line as opening
		if (line.includes("{") && line.includes("}")) {
			const openCount = (line.match(/{/g) || []).length;
			const closeCount = (line.match(/}/g) || []).length;
			indentLevel += openCount - closeCount;
		}
	}

	// Add spacing around operators
	let result = formattedLines.join("\n");

	// Add space after commas if not already present
	result = result.replace(/,(\S)/g, ", $1");

	// Add space around binary operators (=, +, -, *, /, ==, !=, etc.)
	result = result.replace(/(\w)([+\-*/%]=?)(\w)/g, "$1 $2 $3");
	result = result.replace(/(\w)(==|!=|<=|>=|&&|\|\|)(\w)/g, "$1 $2 $3");

	// Add space after keywords (if, for, while, etc.)
	result = result.replace(/\b(if|for|while|switch|catch)\(/g, "$1 (");

	// Remove space before semicolons
	result = result.replace(/\s+;/g, ";");

	// Ensure single space after semicolon in for loops
	result = result.replace(/;(\S)/g, "; $1");

	return result;
}

export const javaFormattingProvider: languages.DocumentFormattingEditProvider = {
	provideDocumentFormattingEdits(model) {
		const formatted = formatJavaCode(model.getValue());

		return [
			{
				range: model.getFullModelRange(),
				text: formatted,
			},
		];
	},
};
