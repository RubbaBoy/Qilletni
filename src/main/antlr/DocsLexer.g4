lexer grammar DocsLexer;

@lexer::header {
    package is.yarr.qilletni.antlr;
}

BEGIN_DOC: '/**' -> skip;
END_DOC: '*/' -> skip;
//BEGIN_LINE: '*';
//DOC_TEXT

//RETURNS: '@returns';
//PARAM: '@param';
//TYPE: '@type';
//

L_BRACKET: '[' -> pushMode(IN_BRACKETS);
R_BRACKET: ']';


// Define tokens for different parts of the text
PARAM: '@param' -> pushMode(FUN_PARAM_MODE);
RETURNS: '@returns';
JAVA: '@java';
TYPE: '@type';
ON: '@on';
ERRORS: '@errors';

//IDENTIFIER: [a-zA-Z_][a-zA-Z0-9_]*;
NEWLINE: [\r\n] -> channel(HIDDEN); // Skip newlines globally
WS: [ \t]+ -> skip; // Skip whitespace globally

TEXT: ~[@\\[]+; // Match any text except lines starting with '@'
//TEXT: ~[\r\n@\\[]+; // Match any text except newlines and lines starting with '@'

//DESCRIPTION: ~[\r\n]+;

mode IN_BRACKETS;
//BRACKETS_PARAM: '@param' -> type(PARAM);
BRACKETS_TEXT: [_a-zA-Z0-9][ _a-zA-Z0-9\\.]+;
INNER_R_BRACKET: ']' -> type(R_BRACKET), popMode;
B_PARAM: '@param' -> type(PARAM);
B_RETURNS: '@returns' -> type(RETURNS);
B_JAVA: '@java' -> type(JAVA);
B_TYPE: '@type' -> type(TYPE);
B_WS: [ \t]+ -> skip; // Skip whitespace globally

mode FUN_PARAM_MODE;
P_WS: [ \t]+ -> skip; // Skip whitespace globally
PARAM_NAME: ~[\\[\r\n ]+ -> popMode;
P_L_BRACKET: '[' -> type(L_BRACKET), pushMode(IN_BRACKETS);
