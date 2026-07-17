# EXPRESSIONS
AST nodes:
Expr -> Literal | Unary | Binary | Grouping
Literal -> NUMBER | STRING | "true" | "false" | "nil"
Grouping -> "(" Expr ")"
Unary -> ( "-" | "!" ) Expr
Binary -> Expr operator Expr

With precedence:
expression     → list ;
list           → equality ( "," equality )* ;
equality       → comparison ( ( "!=" | "==" ) comparison )* ;
comparison     → sum ( ( ">" | ">=" | "<" | "<=" ) sum )* ;
sum            → product ( ( "-" | "+" ) product )* ;
product        → unary ( ( "/" | "\*" ) unary )* ;
unary          → ( "!" | "-" ) unary
               | primary ;
primary        → NUMBER | STRING | "true" | "false" | "nil"
               | "(" expression ")" ;
