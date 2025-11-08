import type { languages } from 'monaco-editor';

export const javaTokenizer: languages.IMonarchLanguage = {
    defaultToken: '',
    tokenPostfix: '.java',

    // Control flow keywords (pink)
    controlKeywords: [
        'if', 'else', 'for', 'while', 'do', 'switch', 'case', 'default',
        'break', 'continue', 'return', 'throw', 'try', 'catch', 'finally',
        'new', 'instanceof'
    ],

    // Declaration keywords (blue)
    declarationKeywords: [
        'class', 'interface', 'enum', 'extends', 'implements',
        'public', 'private', 'protected', 'static', 'final', 'abstract',
        'synchronized', 'volatile', 'transient', 'native', 'strictfp'
    ],

    // Primitive types (green)
    primitiveTypes: [
        'int', 'long', 'short', 'byte', 'char', 'float', 'double', 'boolean', 'void'
    ],

    // Other keywords
    otherKeywords: [
        'this', 'super', 'null', 'true', 'false', 'assert', 'package', 'import', 'const', 'goto'
    ],

    standardTypes: [
        // Collections
        'ArrayList', 'HashMap', 'HashSet', 'LinkedList', 'TreeMap', 'TreeSet',
        'PriorityQueue', 'Stack', 'Queue', 'Deque', 'List', 'Set', 'Map',
        'Collection', 'Iterator', 'Comparator', 'Collections', 'Arrays',
        // Common types
        'String', 'Integer', 'Long', 'Double', 'Float', 'Boolean', 'Character',
        'Byte', 'Short', 'Object', 'Class', 'System', 'Math', 'StringBuilder',
        'StringBuffer', 'Number', 'Comparable', 'Enum', 'Exception', 'Thread',
        'Runnable', 'Optional', 'Stream'
    ],

    operators: [
        '=', '>', '<', '!', '~', '?', ':', '==', '<=', '>=', '!=',
        '&&', '||', '++', '--', '+', '-', '*', '/', '&', '|', '^', '%',
        '<<', '>>', '>>>', '+=', '-=', '*=', '/=', '&=', '|=', '^=',
        '%=', '<<=', '>>=', '>>>='
    ],

    symbols: /[=><!~?:&|+\-*\/\^%]+/,
    escapes: /\\(?:[abfnrtv\\"']|x[0-9A-Fa-f]{1,4}|u[0-9A-Fa-f]{4}|U[0-9A-Fa-f]{8})/,

    tokenizer: {
        root: [
            // Standard library types and class names
            [/[A-Z][\w]*/, {
                cases: {
                    '@standardTypes': 'type.predefined',
                    '@default': 'type.identifier'
                }
            }],

            // identifiers followed by ( - check keywords first, then treat as method
            [/[a-z_$][\w$]*(?=\s*\()/, {
                cases: {
                    '@controlKeywords': 'keyword.control',
                    '@declarationKeywords': 'keyword.declaration',
                    '@primitiveTypes': 'keyword.type',
                    '@otherKeywords': 'keyword',
                    '@default': 'identifier.method'
                }
            }],

            // all other identifiers and keywords
            [/[a-z_$][\w$]*/, {
                cases: {
                    '@controlKeywords': 'keyword.control',
                    '@declarationKeywords': 'keyword.declaration',
                    '@primitiveTypes': 'keyword.type',
                    '@otherKeywords': 'keyword',
                    '@default': 'identifier'
                }
            }],

            // whitespace
            { include: '@whitespace' },

            // delimiters and operators
            [/[{}()\[\]]/, '@brackets'],
            [/[<>](?!@symbols)/, '@brackets'],
            [/@symbols/, {
                cases: {
                    '@operators': 'operator',
                    '@default': ''
                }
            }],

            // numbers
            [/\d*\.\d+([eE][\-+]?\d+)?[fFdD]?/, 'number.float'],
            [/0[xX][0-9a-fA-F_]*[0-9a-fA-F][Ll]?/, 'number.hex'],
            [/0[0-7_]*[0-7][Ll]?/, 'number.octal'],
            [/0[bB][0-1_]*[0-1][Ll]?/, 'number.binary'],
            [/\d+[lL]?/, 'number'],

            // delimiter: after number because of .\d floats
            [/[;,.]/, 'delimiter'],

            // strings
            [/"([^"\\]|\\.)*$/, 'string.invalid'],
            [/"/, 'string', '@string'],

            // characters
            [/'[^\\']'/, 'string'],
            [/(')(@escapes)(')/, ['string', 'string.escape', 'string']],
            [/'/, 'string.invalid']
        ],

        whitespace: [
            [/[ \t\r\n]+/, ''],
            [/\/\*\*(?!\/)/, 'comment.doc', '@javadoc'],
            [/\/\*/, 'comment', '@comment'],
            [/\/\/.*$/, 'comment'],
        ],

        comment: [
            [/[^\/*]+/, 'comment'],
            [/\*\//, 'comment', '@pop'],
            [/[\/*]/, 'comment']
        ],

        javadoc: [
            [/[^\/*]+/, 'comment.doc'],
            [/\*\//, 'comment.doc', '@pop'],
            [/[\/*]/, 'comment.doc']
        ],

        string: [
            [/[^\\"]+/, 'string'],
            [/@escapes/, 'string.escape'],
            [/\\./, 'string.escape.invalid'],
            [/"/, 'string', '@pop']
        ],
    },
};
