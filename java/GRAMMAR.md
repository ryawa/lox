# EXPRESSIONS
AST nodes:
Expr -> Literal | Unary | Binary | Grouping | Ternary
Literal -> NUMBER | STRING | "true" | "false" | "nil"
Grouping -> "(" Expr ")"
Unary -> ( "-" | "!" ) Expr
Binary -> Expr operator Expr
Ternary -> Expr operator Expr operator Expr

With precedence:
expression     → list ;
list           → conditional ( "," conditional )* ;
conditional    → equality ( "?" expression ":" conditional )? ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → sum ( ( ">" | ">=" | "<" | "<=" ) sum )* ;
sum            → product ( ( "-" | "+" ) product )* ;
product        → unary ( ( "/" | "\*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
               | "(" expression ")" ;
