lexer grammar QilletniLexer;

@lexer::header {
    package is.yarr.qilletni.antlr;
}

tokens {
    STRING_VALUE
}

IMPORT: 'import';
AS: 'as';

LINE_COMMENT: '//' .*? NEWLINE -> skip;
DOC_COMMENT: '/**' .*? '*/';
BLOCK_COMMENT: '/*' .*? '*/' -> skip;

// var name


PLUS : '+';
INCREMENT : '++';
DECREMENT : '--';
PLUS_EQUALS : '+=';
MINUS_EQUALS : '-=';
// ~/ is int division
OP            : '*' | '/~' | '%' | '-';
DIV_DOUBLE_OP : '/';

WEIGHTS_KEYWORD : 'weights';

// keywords
INT_TYPE : 'int';
DOUBLE_TYPE : 'double';
STRING_TYPE : 'string';
BOOLEAN_TYPE : 'boolean';
COLLECTION_TYPE : 'collection';
SONG_TYPE : 'song';
ALBUM_TYPE : 'album';
JAVA_TYPE : 'java';

ENTITY: 'entity';
EMPTY: 'empty';
NEW: 'new';

ORDER_PARAM   : 'order' -> pushMode(ORDER_MODE);
LIMIT_PARAM   : 'limit' -> pushMode(LIMIT_MODE);
LOOP_PARAM   : 'loop';

IF_KEYWORD: 'if';
ELSE_KEYWORD: 'else';
FOR_KEYWORD: 'for';
RANGE_INFINITY: 'infinity';
RANGE_OP: '..';
COLON: ':';


PLAY : 'play';
PROVIDER : 'provider';
FUNCTION_DEF : 'fun';
STATIC : 'static';
NATIVE : 'native';
ON: 'on';
DOT: '.';

NEWLINE           : [\r\n]+ -> skip;
WS                : [ \t\n\r]+ -> skip;
BOOL              : 'true' | 'false';
REL_OP            : '>' | '<' | '>=' | '<=' | '==' | '!=';
LEFT_SBRACKET     : '[';
RIGHT_SBRACKET    : ']';
LEFT_PAREN  : '(';
RIGHT_PAREN : ')';
LEFT_CBRACKET : '{';
RIGHT_CBRACKET : '}';
COMMA : ',';
ASSIGN : '=';

RETURN : 'return';



BY            : 'by';
CREATED       : 'created';
//STR_LITERAL   : '"' STR '"';

LQUOTE : '"' -> more, mode(STR);


mode STR;
STRING : '"' -> mode(DEFAULT_MODE);
TEXT   : . -> more;

mode DEFAULT_MODE;

ID            : [a-zA-Z_][a-zA-Z_0-9]*;
INT           : [0-9]+ ;
DOUBLE          : ([0-9]+'D' | [0-9]+'.'[0-9]+'D'?);
fragment STR  : (ESC | ~["\\]);
fragment ESC: '\\' [\\"];


WEIGHT_PIPE: ('|' | '|!' | '|~') -> pushMode(WEIGHT_MODE);

mode ORDER_MODE;
O_WS          : [ \t\n\r]+ -> skip;
O_LEFT_SBRACKET   : '[' -> type(LEFT_SBRACKET);
COLLECTION_ORDER  : 'shuffle' | 'sequential';
O_RIGHT_SBRACKET    : ']' -> type(RIGHT_SBRACKET), popMode;

mode LIMIT_MODE;
L_WS          : [ \t\n\r]+ -> skip;
LIMIT_NUMBER: INT -> type(INT);
L_LEFT_SBRACKET   : '[' -> type(LEFT_SBRACKET);
LIMIT_UNIT        : 's' | 'm' | 'h';
L_RIGHT_SBRACKET    : ']' -> type(RIGHT_SBRACKET), popMode;

mode WEIGHT_MODE;
W_WS          : [ \t\n\r]+ -> skip;
WEIGHT_NUMBER : INT -> type(INT);
WEIGHT_UNIT   : ('x' | '%') -> popMode;
