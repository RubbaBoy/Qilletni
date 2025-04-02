parser grammar DocsParser;

@header {
    package dev.qilletni.impl.antlr;
}

options { tokenVocab=DocsLexer; }

docText: textLine? paramLine* returnsLine? typeLine? onLine? errorsLine?;

// General description of what's being documented.
textLine: description;

// Describes params of the function
paramLine: PARAM inline_brackets? PARAM_NAME description;

// Describe the return value of the function
returnsLine: RETURNS inline_brackets? description;

// Provide the type (if this is documenting a field) not a description
typeLine: TYPE JAVA? TEXT;

// If the function is "on" a type, describe it
onLine: ON inline_brackets? description;

// On functions, if it can error
errorsLine: ERRORS description;

description: description_unit+;

description_unit: TEXT+ inline_brackets?;

// Second JAVA only applicable if after TYPE
inline_brackets: L_BRACKET (PARAM | JAVA | TYPE) isJava=JAVA? BRACKETS_TEXT R_BRACKET;
