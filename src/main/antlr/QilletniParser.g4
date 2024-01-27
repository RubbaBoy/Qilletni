parser grammar QilletniParser;

@header {
    package is.yarr.qilletni.antlr;
}

options { tokenVocab=QilletniLexer; }
		
prog
    : import_file* running* EOF;

import_file
    : IMPORT STRING
    ;

running
    : body_stmt
    | function_def
    | NEWLINE
    ;

// Expressions
expr: LEFT_PAREN expr RIGHT_PAREN
    | ID LEFT_SBRACKET int_expr RIGHT_SBRACKET
    | expr REL_OP expr
    | expr PLUS expr
    | expr DOT function_call
    | expr DOT ID
    | entity_initialize
    | function_call
    | ID
    | bool_expr
    | int_expr
    | str_expr
    | collection_expr
    | song_expr
    | album_expr
    | weights_expr
    | java_expr
    | list_expression
    ;

bool_expr
    : function_call
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
    | STRING
    | str_expr PLUS expr
    | function_call
    | ID
    ;

collection_expr
    : function_call
    | collection_url_or_name_pair order_define? weights_define?
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
    : type=(INT_TYPE | STRING_TYPE | BOOLEAN_TYPE | COLLECTION_TYPE | SONG_TYPE | WEIGHTS_KEYWORD | ALBUM_TYPE | JAVA_TYPE | ID)? LEFT_SBRACKET expr_list? RIGHT_SBRACKET
    | ID
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
    : type=INT_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID ASSIGN expr
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
    : PLAY song_expr
    | PLAY collection_expr collection_limit? LOOP_PARAM?
    ;

function_def
    : FUNCTION_DEF ID '(' function_def_params ')' function_on_type? '{' body '}'
    | NATIVE FUNCTION_DEF ID '(' function_def_params ')' function_on_type?
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
    : bool_expr
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
    : ENTITY ID '{' entity_body '}'
    ;

entity_body
    : entity_property_declaration* entity_constructor? function_def*
    ;

entity_property_declaration
    : type=INT_TYPE ID (ASSIGN int_expr)?
    | type=STRING_TYPE ID (ASSIGN str_expr)?
    | type=BOOLEAN_TYPE ID (ASSIGN bool_expr)?
    | type=COLLECTION_TYPE ID (ASSIGN collection_expr)?
    | type=SONG_TYPE ID (ASSIGN song_expr)?
    | type=WEIGHTS_KEYWORD ID (ASSIGN weights_expr)?
    | type=ALBUM_TYPE ID (ASSIGN album_expr)?
    | type=JAVA_TYPE ID (ASSIGN java_expr)?
    | type=ID ID (ASSIGN entity_initialize)?
    ;

entity_constructor
    : ID '(' function_def_params ')'
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
    ;


weight_amount
    : INT WEIGHT_UNIT
    ;

limit_amount
    : INT LIMIT_UNIT?
    ;