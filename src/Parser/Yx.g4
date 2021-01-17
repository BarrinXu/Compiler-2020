grammar Yx;

program: (classDef | declarationStmt | functionDef)*;

classDef : Class Identifier '{' (declarationStmt | functionDef | constructorDef)* '}' ';';

constructorDef : Identifier '(' ')' suite;

functionDef : returnType Identifier '(' functionParameterDef ')' suite;

functionParameterDef : (varType Identifier (',' varType Identifier)* )?;


expressionList : expression (',' expression)*;

suite : '{' statement* '}';

statement
    : suite                                                 #block
    | declarationStmt                                       #varDefStmt
    | While '(' expression ')' statement                    #whileStmt
    | If '(' expression ')' trueStmt=statement
        (Else falseStmt=statement)?                         #ifStmt
    | For '(' initExp = exprOrvarDef? ';'
        condExp = expression? ';'
        updExp = expression? ')'
        statement                                           #forStmt
    | Break ';'                                             #breakStmt
    | Continue ';'                                          #continueStmt

    | Return expression? ';'                                #returnStmt
    | expression ';'                                        #pureExprStmt
    | ';'                                                   #emptyStmt
    ;

declarationStmt : varDef ';';

expression
    : primary                                               #atomExpr
    | New (arraySemanticError | arrayLiteral | constructorCall | Identifier)     #newExpr
    | expression '.' Identifier                             #memberExpr
    | expression '[' expression ']'                         #indexExpr
    | expression '(' expressionList? ')'                    #functionExpr
    | expression op = ('++' | '--')                         #suffixExpr
    | <assoc=right>
        op = ('++' | '--' | '+' | '-' | '!' | '~')
        expression                                          #prefixExpr
    | expression op = ('*' | '/' | '%') expression          #binaryExpr
    | expression op = ('+' | '-') expression                #binaryExpr
    | expression op = ('<<' | '>>') expression              #binaryExpr
    | expression op = ('<' | '<=' | '>' | '>=') expression  #binaryExpr
    | expression op = ('==' | '!=') expression              #binaryExpr
    | expression op = '&' expression                        #binaryExpr
    | expression op = '^' expression                        #binaryExpr
    | expression op = '|' expression                        #binaryExpr
    | expression op = '&&' expression                       #binaryExpr
    | expression op = '||' expression                       #binaryExpr
    | <assoc=right> expression '=' expression               #assignExpr
    ;

arraySemanticError : (builtinType | Identifier) ('[' expression ']')+ ('['']')+ ('[' expression ']')+;
arrayLiteral : (builtinType | Identifier) ('[' expression ']')+ ('['']')*;
constructorCall : Identifier '(' expressionList? ')';

exprOrvarDef : expression | varDef;
varDef : varType varDeclaration (',' varDeclaration)*;
varDeclaration : Identifier ('=' expression)?;

returnType: Void | varType;
varType : (builtinType | Identifier) ('[' ']')*;
builtinType : Int | Bool | String;

primary
    : '(' expression ')'
    | Identifier
    | literal
    ;

literal
    : DecimalInteger
    | StringType
    | Null
    | True
    | False
    | This
    ;



Int : 'int';
Bool : 'bool';
String : 'string';
Null : 'null';
Void : 'void';
True : 'true';
False : 'false';
If : 'if';
Else : 'else';
For : 'for';
While : 'while';
Break : 'break';
Continue : 'continue';
Return : 'return';
New : 'new';
Class : 'class';
This : 'this';

Dot : '.';
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

Mul : '*';
Div : '/';
Mod : '%';

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

BackSlash : '\\\\';
DbQuotation : '\\"';

Identifier
    : [a-zA-Z] [a-zA-Z_0-9]*
    ;

DecimalInteger
    : [1-9] [0-9]*
    | '0'
    ;

StringType : '"' (BackSlash | DbQuotation | .)*? '"';

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