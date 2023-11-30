parser grammar QilletniParser;

@header {
    package is.yarr.qilletni.antlr;
}

options { tokenVocab=QilletniLexer; }
		
prog
    : import_file* running+ EOF;

import_file
    : IMPORT STRING
    ;

running
    : body_stmt
    | function_def
    | NEWLINE
    | COMMENT
    ;

// Expressions
expr: LEFT_PAREN expr RIGHT_PAREN
    | ID PLUS ID // handled separately due to appending of different types
    | function_call
    | ID
    | bool_expr
    | int_expr
    | str_expr
    | collection_expr
    | song_expr
    ;

bool_expr
    : int_expr REL_OP int_expr
    | function_call
    | ID
    | BOOL
    ;

int_expr
    : int_expr PLUS int_expr
    | int_expr OP int_expr
    | LEFT_PAREN int_expr RIGHT_PAREN
    | function_call
    | INT
    | ID
    ;

str_expr
    : LEFT_PAREN str_expr RIGHT_PAREN
    | ID
    | STRING
    | str_expr PLUS expr
    | function_call
    ;

collection_expr
    : ID
    | function_call
    | stateful_collection_define
    ;

stateful_collection_define
    : url_or_name_pair order_define? weights_define?
    ;

order_define
    : ORDER_PARAM LEFT_SBRACKET COLLECTION_ORDER RIGHT_SBRACKET
    ;

weights_define
    : WEIGHTS_KEYWORD LEFT_SBRACKET (ID | function_call) RIGHT_SBRACKET
    ;

song_expr
    : ID
    | function_call
    | url_or_name_pair
    ;

url_or_name_pair
    : URL_LITERAL
    | STRING BY STRING
    ;

weights_expr
    : single_weight single_weight_t
    ;

single_weight
    : WEIGHT_PIPE weight_amount url_or_name_pair
    ;

single_weight_t
    : single_weight single_weight_t
    | // epsilon
    ;

function_call
    : ID '(' expr_list? ')'
    ;

expr_list
    : expr (',' expr)*
    ;

body_stmt
    : if_stmt
    | for_stmt
    | stmt
    ;

return_stmt
    : RETURN expr
    ;

body
    : body_stmt body
    | return_stmt
    | COMMENT body
    | // epsilon
    ;

asmt
    : type=INT_TYPE ID ASSIGN int_expr
    | type=STRING_TYPE ID ASSIGN str_expr
    | type=BOOLEAN_TYPE ID ASSIGN bool_expr
    | type=COLLECTION_TYPE ID ASSIGN collection_expr
    | type=SONG_TYPE ID ASSIGN song_expr
    | type=WEIGHTS_KEYWORD ID ASSIGN weights_expr
    | ID ASSIGN expr
    ;

collection_limit
    : LIMIT_PARAM LEFT_SBRACKET limit_amount RIGHT_SBRACKET
    ;

play_stmt
    : PLAY url_or_name_pair
    | PLAY ID collection_limit? // collection_limit is only for collection
    | PLAY COLLECTION_TYPE stateful_collection_define collection_limit?
    ;

function_def
    : FUNCTION_DEF ID '(' function_def_params ')' '{' body '}'
    | NATIVE FUNCTION_DEF ID '(' function_def_params ')'
    ;

function_def_params
    : ID (',' ID)*
    |
    ;

if_stmt
    : IF_KEYWORD '('  bool_expr ')' '{' body '}' elseif_list else_body
    ;

else_body
    : ELSE_KEYWORD '{' body '}'
    | // epsilon
    ;

elseif_list
    : ELSE_KEYWORD IF_KEYWORD '(' bool_expr ')' '{' body '}' elseif_list
    | // epsilon
    ;

for_stmt
    : FOR_KEYWORD '(' for_expr ')' '{' body '}'
    ;

for_expr
    : bool_expr
    | range
    ;

range
    : ID RANGE_OP (INT | RANGE_INFINITY)
    ;

stmt
    : play_stmt
    | asmt
    | function_call
    ;


weight_amount
    : INT WEIGHT_UNIT
    ;

limit_amount
    : INT LIMIT_UNIT?
    ;