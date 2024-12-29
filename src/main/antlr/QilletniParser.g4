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
// TODO: Add support for foo.bar[12]
expr
    // 1) Parenthesized expression
    : LEFT_PAREN expr RIGHT_PAREN

    // 2) A function call with no preceding expr
    | function_call

    // 3) Pre/post increment on an ID with optional indexing: ++x, x++, x[expr]++, etc.
    | id_pre_crement=(INCREMENT | DECREMENT)? ID LEFT_SBRACKET expr RIGHT_SBRACKET id_post_crement=(INCREMENT | DECREMENT)?
    | id_pre_crement=(INCREMENT | DECREMENT)? ID id_post_crement=(INCREMENT | DECREMENT)?

    // 4) Postfix increment with += or -= on an ID or an ID with indexing
    | ID LEFT_SBRACKET expr RIGHT_SBRACKET id_post_crement_equals=(PLUS_EQUALS | MINUS_EQUALS) expr
    | ID id_post_crement_equals=(PLUS_EQUALS | MINUS_EQUALS) expr

    // 5) Dot notation: expr DOT function_call, or expr DOT ID (with optional increments)
    | expr DOT function_call
    | expr DOT ID post_crement=(INCREMENT | DECREMENT)?
    | expr DOT ID post_crement_equals=(PLUS_EQUALS | MINUS_EQUALS) expr

    // 6) Entity initialize
    | entity_initialize

    // 8) Logical NOT
    | NOT expr

    // 9) Relational compare: expr REL_OP expr (>, <, >=, <=, ==, !=)
    | expr REL_OP expr

    // 10) Boolean literal
    | BOOL

    // 11) The new entry point for arithmetic: an additive expression
    | addSubExpr

    // 12) Additional expressions from original grammar
    | str_expr
    | int_expr
    | double_expr
    | collection_expr
    | song_expr
    | album_expr
    | weights_expr
    | java_expr
    | is_expr
    | list_expression
    
    // 7) Standalone ID
    | ID

    ;

/**
 *  For standard arithmetic precedence:
 *   - addSubExpr handles +, -
 *   - mulDivExpr handles *, /, /~, %
 *   - unaryArithmetic handles any prefix ops if you choose to place them there
 *
 *  This ensures multiplication/division have higher precedence than addition/subtraction.
 */
addSubExpr
    : mulDivExpr ( (PLUS | MINUS) mulDivExpr )*
    ;

mulDivExpr
    : unaryArithmetic ( (STAR | FLOOR_DIV | DIV | MOD) unaryArithmetic )*
    ;

/**
 *  Other unary operators could be added, such as::
 *    unaryArithmetic
 *      : (INCREMENT | DECREMENT) unaryArithmetic
 *      | primaryArithmetic
 *      ;
 */
unaryArithmetic
    : primaryArithmetic
    ;

primaryArithmetic
    : LEFT_PAREN expr RIGHT_PAREN
    | function_call
    | int_expr
    | double_expr
    | str_expr
    | list_expression
    | ID
    ;

int_expr
    : INT
    // Cast to int
    | INT_TYPE LEFT_PAREN expr RIGHT_PAREN
    ;

double_expr
    : DOUBLE
    // Cast to int
    | DOUBLE_TYPE LEFT_PAREN expr RIGHT_PAREN
    ;

str_expr
    : STRING
     // Cast to string
    | STRING_TYPE LEFT_PAREN expr RIGHT_PAREN
    ;

collection_expr
    : 
    collection_url_or_name_pair order_define? weights_define?
    | COLLECTION_TYPE LEFT_PAREN list_expression RIGHT_PAREN order_define? weights_define?
    | STRING
    ;

order_define
    : ORDER_PARAM LEFT_SBRACKET COLLECTION_ORDER RIGHT_SBRACKET
    ;

weights_define
    : WEIGHTS_KEYWORD LEFT_SBRACKET (ID | function_call) RIGHT_SBRACKET
    ;

song_expr
    : song_url_or_name_pair
    | STRING
    ;

album_expr
    : album_url_or_name_pair
    | STRING
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
    ;

single_weight
    : WEIGHT_PIPE weight_amount expr
    ;

list_expression
    : type=(ANY_TYPE | INT_TYPE | DOUBLE_TYPE | STRING_TYPE | BOOLEAN_TYPE | COLLECTION_TYPE | SONG_TYPE | WEIGHTS_KEYWORD | ALBUM_TYPE | JAVA_TYPE | ID)? LEFT_SBRACKET expr_list? RIGHT_SBRACKET
    ;

is_expr
    : ID IS_KEYWORD (type=(ANY_TYPE | INT_TYPE | DOUBLE_TYPE | STRING_TYPE | BOOLEAN_TYPE | COLLECTION_TYPE | SONG_TYPE | WEIGHTS_KEYWORD | ALBUM_TYPE | JAVA_TYPE | ID)? (LEFT_SBRACKET RIGHT_SBRACKET)?)
    ;

java_expr
    : EMPTY
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
    | expr DOUBLE_DOT ID ASSIGN expr
    | asmt DOUBLE_DOT ID ASSIGN expr
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
    | // epsilon
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
    : ID DOUBLE_DOT (expr | RANGE_INFINITY)
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
    : DOC_COMMENT? type=ANY_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=INT_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=DOUBLE_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=STRING_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=BOOLEAN_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=COLLECTION_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=SONG_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=WEIGHTS_KEYWORD (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=ALBUM_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=JAVA_TYPE (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
    | DOC_COMMENT? type=ID (LEFT_SBRACKET RIGHT_SBRACKET)? ID (ASSIGN expr)?
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