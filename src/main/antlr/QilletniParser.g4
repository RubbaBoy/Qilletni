parser grammar QilletniParser;

@header {
    package is.yarr.qilletni.antlr;
}

options { tokenVocab=QilletniLexer; }
		
prog
    : import_file* running* EOF;

import_file
    : IMPORT STRING (AS ID)?
    ;

running
    : body_stmt
    | function_def
    | NEWLINE
    ;

// Expressions
expr: LEFT_PAREN expr RIGHT_PAREN
    | function_call
    | pre_crement=(INCREMENT | DECREMENT)? ID LEFT_SBRACKET expr RIGHT_SBRACKET post_crement=(INCREMENT | DECREMENT)?
    | pre_crement=(INCREMENT | DECREMENT)? ID post_crement=(INCREMENT | DECREMENT)?
    | ID LEFT_SBRACKET expr RIGHT_SBRACKET post_crement_equals=(PLUS_EQUALS | MINUS_EQUALS) expr
    | ID post_crement_equals=(PLUS_EQUALS | MINUS_EQUALS) expr
    | expr PLUS expr
    | expr DOT function_call
    | expr DOT ID post_crement=(INCREMENT | DECREMENT)?
    | expr DOT ID post_crement_equals=(PLUS_EQUALS | MINUS_EQUALS) expr
    | entity_initialize
    | ID
    | expr REL_OP expr
    | BOOL
    | int_expr
    | double_expr
    | str_expr
    | collection_expr
    | song_expr
    | album_expr
    | weights_expr
    | java_expr
    | list_expression
    | is_expr
    ;

//bool_expr
//    : function_call
//    | expr REL_OP (int_expr | double_expr)
//    | BOOL
//    ;

int_expr
    : int_expr op=(OP | PLUS) int_expr
    | wrap=LEFT_PAREN int_expr RIGHT_PAREN
    | INT_TYPE LEFT_PAREN double_expr RIGHT_PAREN
    | function_call
    | INT
    | ID
    ;

double_expr
    : int_expr ii_op=DIV_DOUBLE_OP int_expr
    | double_expr dd_op=(OP | PLUS | DIV_DOUBLE_OP) double_expr
    | double_expr di_op=(OP | PLUS | DIV_DOUBLE_OP) int_expr
    | int_expr id_op=(OP | PLUS | DIV_DOUBLE_OP) double_expr
    | wrap=LEFT_PAREN double_expr RIGHT_PAREN
    | DOUBLE_TYPE LEFT_PAREN int_expr RIGHT_PAREN
    | function_call
    | DOUBLE
    | ID
    ;

str_expr
    : LEFT_PAREN str_expr RIGHT_PAREN
    | STRING
    | str_expr PLUS expr
    | function_call
    | ID
    ;

collection_expr
    : function_call
    | collection_url_or_name_pair order_define? weights_define?
    | COLLECTION_TYPE LEFT_PAREN list_expression RIGHT_PAREN order_define? weights_define?
    | STRING
    | ID
    ;

order_define
    : ORDER_PARAM LEFT_SBRACKET COLLECTION_ORDER RIGHT_SBRACKET
    ;

weights_define
    : WEIGHTS_KEYWORD LEFT_SBRACKET (ID | function_call) RIGHT_SBRACKET
    ;

song_expr
    : function_call
    | song_url_or_name_pair
    | STRING
    | ID
    ;

album_expr
    : function_call
    | album_url_or_name_pair
    | STRING
    | ID
    ;

song_url_or_name_pair
    : STRING SONG_TYPE? BY STRING
    ;

collection_url_or_name_pair
    : STRING COLLECTION_TYPE BY STRING
    ;

album_url_or_name_pair
    : STRING ALBUM_TYPE BY STRING
    ;

weights_expr
    : single_weight single_weight*
    | ID
    ;

single_weight
    : WEIGHT_PIPE weight_amount expr
    ;

list_expression
    : type=(ANY_TYPE | INT_TYPE | DOUBLE_TYPE | STRING_TYPE | BOOLEAN_TYPE | COLLECTION_TYPE | SONG_TYPE | WEIGHTS_KEYWORD | ALBUM_TYPE | JAVA_TYPE | ID)? LEFT_SBRACKET expr_list? RIGHT_SBRACKET
    | ID
    ;

is_expr
    : ID IS_KEYWORD (type=(ANY_TYPE | INT_TYPE | DOUBLE_TYPE | STRING_TYPE | BOOLEAN_TYPE | COLLECTION_TYPE | SONG_TYPE | WEIGHTS_KEYWORD | ALBUM_TYPE | JAVA_TYPE | ID)? | (LEFT_SBRACKET RIGHT_SBRACKET)?)
    ;

java_expr
    : function_call
    | EMPTY
    | ID
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
    | expr
    ;

return_stmt
    : RETURN expr
    ;

body
    : body_stmt body
    | return_stmt
    | // epsilon
    ;

asmt
    : type=ANY_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=INT_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=DOUBLE_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=STRING_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=BOOLEAN_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=COLLECTION_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=SONG_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=WEIGHTS_KEYWORD (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=ALBUM_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=JAVA_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | type=ID (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
    | ID LEFT_SBRACKET int_expr RIGHT_SBRACKET ASSIGN expr
    | ID ASSIGN expr
    | expr_assign=expr DOT ID ASSIGN expr
    ;

collection_limit
    : LIMIT_PARAM LEFT_SBRACKET limit_amount RIGHT_SBRACKET
    ;

play_stmt
    : PLAY (ID | expr) collection_limit? LOOP_PARAM?
    ;

provider_stmt
    : PROVIDER str_expr ('{' body '}')?
    ;

function_def
    : DOC_COMMENT? STATIC? FUNCTION_DEF ID '(' function_def_params ')' function_on_type? '{' body '}'
    | DOC_COMMENT? NATIVE STATIC? FUNCTION_DEF ID '(' function_def_params ')' function_on_type?
    ;

function_on_type
    : ON type=(INT_TYPE | STRING_TYPE | BOOLEAN_TYPE | COLLECTION_TYPE | SONG_TYPE | ALBUM_TYPE | WEIGHTS_KEYWORD | ID)
    ;

function_def_params
    : ID (',' ID)*
    |
    ;

if_stmt
    : IF_KEYWORD '('  expr ')' '{' body '}' elseif_list else_body
    ;

else_body
    : ELSE_KEYWORD '{' body '}'
    | // epsilon
    ;

elseif_list
    : ELSE_KEYWORD IF_KEYWORD '(' expr ')' '{' body '}' elseif_list
    | // epsilon
    ;

for_stmt
    : FOR_KEYWORD '(' for_expr ')' '{' body '}'
    ;

for_expr
    : expr
    | range
    | foreach_range
    ;

range
    : ID RANGE_OP (INT | RANGE_INFINITY)
    ;

foreach_range
    : ID COLON expr
    ;

entity_def
    : DOC_COMMENT? ENTITY ID '{' entity_body '}'
    ;

entity_body
    : entity_property_declaration* entity_constructor? function_def*
    ;

entity_property_declaration // TODO: lists
    : DOC_COMMENT? type=ANY_TYPE ID (ASSIGN expr)?
    | DOC_COMMENT? type=INT_TYPE ID (ASSIGN int_expr)?
    | DOC_COMMENT? type=STRING_TYPE ID (ASSIGN str_expr)?
    | DOC_COMMENT? type=BOOLEAN_TYPE ID (ASSIGN expr)?
    | DOC_COMMENT? type=COLLECTION_TYPE ID (ASSIGN collection_expr)?
    | DOC_COMMENT? type=SONG_TYPE ID (ASSIGN song_expr)?
    | DOC_COMMENT? type=WEIGHTS_KEYWORD ID (ASSIGN weights_expr)?
    | DOC_COMMENT? type=ALBUM_TYPE ID (ASSIGN album_expr)?
    | DOC_COMMENT? type=JAVA_TYPE ID (ASSIGN java_expr)?
    | DOC_COMMENT? type=ID ID (ASSIGN entity_initialize)?
    ;

entity_constructor
    : DOC_COMMENT? ID '(' function_def_params ')'
    ;

entity_initialize
    : NEW ID '(' expr_list? ')'
    ;

stmt
    : play_stmt
    | asmt
    | function_call
    | entity_def
    | expr DOT function_call
    | provider_stmt
    ;


weight_amount
    : INT WEIGHT_UNIT
    ;

limit_amount
    : INT LIMIT_UNIT?
    ;