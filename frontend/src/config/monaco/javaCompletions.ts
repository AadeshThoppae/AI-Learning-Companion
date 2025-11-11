import type { languages } from 'monaco-editor';

export const createJavaCompletions = (
    monaco: any,
    range: any
): languages.CompletionItem[] => {
    return [
        // Collections
        {
            label: 'ArrayList',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'ArrayList<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Resizable-array implementation of the List interface',
            range: range
        },
        {
            label: 'HashMap',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'HashMap<${1:KeyType}, ${2:ValueType}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Hash table based implementation of the Map interface',
            range: range
        },
        {
            label: 'HashSet',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'HashSet<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Hash table based implementation of the Set interface',
            range: range
        },
        {
            label: 'LinkedList',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'LinkedList<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Doubly-linked list implementation',
            range: range
        },
        {
            label: 'TreeMap',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'TreeMap<${1:KeyType}, ${2:ValueType}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Red-Black tree based NavigableMap implementation',
            range: range
        },
        {
            label: 'TreeSet',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'TreeSet<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'NavigableSet implementation based on a TreeMap',
            range: range
        },
        {
            label: 'PriorityQueue',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'PriorityQueue<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Priority heap implementation',
            range: range
        },
        {
            label: 'Stack',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Stack<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'LIFO stack of objects',
            range: range
        },
        {
            label: 'Queue',
            kind: monaco.languages.CompletionItemKind.Interface,
            insertText: 'Queue<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Collection for holding elements prior to processing',
            range: range
        },
        {
            label: 'Deque',
            kind: monaco.languages.CompletionItemKind.Interface,
            insertText: 'Deque<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Double ended queue',
            range: range
        },
        {
            label: 'List',
            kind: monaco.languages.CompletionItemKind.Interface,
            insertText: 'List<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Ordered collection interface',
            range: range
        },
        {
            label: 'Set',
            kind: monaco.languages.CompletionItemKind.Interface,
            insertText: 'Set<${1:Type}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Collection that contains no duplicate elements',
            range: range
        },
        {
            label: 'Map',
            kind: monaco.languages.CompletionItemKind.Interface,
            insertText: 'Map<${1:KeyType}, ${2:ValueType}>',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Object that maps keys to values',
            range: range
        },
        // Common types
        {
            label: 'String',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'String',
            documentation: 'The String class represents character strings',
            range: range
        },
        {
            label: 'Integer',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Integer',
            documentation: 'The Integer class wraps a value of the primitive type int',
            range: range
        },
        {
            label: 'Long',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Long',
            documentation: 'The Long class wraps a value of the primitive type long',
            range: range
        },
        {
            label: 'Double',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Double',
            documentation: 'The Double class wraps a value of the primitive type double',
            range: range
        },
        {
            label: 'Boolean',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Boolean',
            documentation: 'The Boolean class wraps a value of the primitive type boolean',
            range: range
        },
        {
            label: 'Character',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Character',
            documentation: 'The Character class wraps a value of the primitive type char',
            range: range
        },
        {
            label: 'StringBuilder',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'StringBuilder',
            documentation: 'Mutable sequence of characters',
            range: range
        },
        {
            label: 'Arrays',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Arrays',
            documentation: 'Contains methods for manipulating arrays',
            range: range
        },
        {
            label: 'Collections',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Collections',
            documentation: 'Contains methods that operate on or return collections',
            range: range
        },
        {
            label: 'Math',
            kind: monaco.languages.CompletionItemKind.Class,
            insertText: 'Math',
            documentation: 'Contains methods for performing basic numeric operations',
            range: range
        },
        // Common methods snippets
        {
            label: 'sysout',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: 'System.out.println(${1});',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Print line to console',
            range: range
        },
        {
            label: 'for',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: 'for (int ${1:i} = 0; ${1:i} < ${2:length}; ${1:i}++) {\n\t${3}\n}',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'For loop',
            range: range
        },
        {
            label: 'foreach',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: 'for (${1:Type} ${2:item} : ${3:collection}) {\n\t${4}\n}',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'Enhanced for loop',
            range: range
        },
        {
            label: 'if',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: 'if (${1:condition}) {\n\t${2}\n}',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'If statement',
            range: range
        },
        {
            label: 'while',
            kind: monaco.languages.CompletionItemKind.Snippet,
            insertText: 'while (${1:condition}) {\n\t${2}\n}',
            insertTextRules: monaco.languages.CompletionItemInsertTextRule.InsertAsSnippet,
            documentation: 'While loop',
            range: range
        }
    ];
};
