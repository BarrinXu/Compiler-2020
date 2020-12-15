grammar Yx;

program: 'int main()' suite EOF;

varDef : Int Identifier ('=' expression)? ';';

suite : '{' statement* '}';

statement
    : suite                                                 #block
    | varDef                                                #vardefStmt
    | While '(' expression ')' statement                    #whileStmt
    | If '(' expression ')' trueStmt=statement 
        (Else falseStmt=statement)?                         #ifStmt
    | Break ';'                                             #breakStmt
    | Continue ';'                                          #continueStmt

    | Return expression? ';'                                #returnStmt
    | expression ';'                                        #pureExprStmt
    | ';'                                                   #emptyStmt
    ;

expression
    : primary                                               #atomExpr
    | expression op=('+' | '-') expression                  #binaryExpr
    | expression op=('==' | '!=' ) expression               #binaryExpr
    | <assoc=right> expression '=' expression               #assignExpr
    ;

primary
    : '(' expression ')'
    | ('++'|'--')* Identifier op=('++'|'--')*
    | literal 
    ;

literal
    : DecimalInteger
    ;

Int : 'int';
If : 'if';
Else : 'else';
While : 'while';

Break : 'break';
Continue : 'continue';

Return : 'return';

LeftParen : '(';
RightParen : ')';
LeftBracket : '[';
RightBracket : ']';
LeftBrace : '{';
RightBrace : '}';

Less : '<';
LessEqual : '<=';
Greater : '>';
GreaterEqual : '>=';
LeftShift : '<<';
RightShift : '>>';

Plus : '+';
SelfPlus : '++';
Minus : '-';
SelfMinus : '--';

And : '&';
Or : '|';
AndAnd : '&&';
OrOr : '||';
Caret : '^';
Not : '!';
Tilde : '~';

Question : '?';
Colon : ':';
Semi : ';';
Comma : ',';

Assign : '=';
Equal : '==';
NotEqual : '!=';

Identifier
    : [a-zA-Z] [a-zA-Z_0-9]*
    ;

DecimalInteger
    : [1-9] [0-9]*
    | '0'
    ;

Whitespace
    :   [ \t]+
        -> skip
    ;

Newline
    :   (   '\r' '\n'?
        |   '\n'
        )
        -> skip
    ;

BlockComment
    :   '/*' .*? '*/'
        -> skip
    ;

LineComment
    :   '//' ~[\r\n]*
        -> skip
    ;