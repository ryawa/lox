# EXPRESSIONS
Expr -> Literal | Unary | Binary | Grouping
Literal -> NUMBER | STRING | "true" | "false" | "nil"
Grouping -> "(" Expr ")"
Unary -> ( "-" | "!" ) Expr
Binary -> Expr operator Expr
