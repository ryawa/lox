package lox;

class AstPrinter implements Expr.Operation<String> {
    String print(Expr expr) {
        return expr.apply(this);
    }

    @Override
    public String onBinaryExpr(Expr.Binary expr) {
        return parenthesize(expr.operator.lexeme, expr.left, expr.right);
    }

    @Override
    public String onGroupingExpr(Expr.Grouping expr) {
        return parenthesize("group", expr.expression);
    }

    @Override
    public String onLiteralExpr(Expr.Literal expr) {
        if (expr.value == null) {
            return "nil";
        }
        return expr.value.toString();
    }

    @Override
    public String onUnaryExpr(Expr.Unary expr) {
        return parenthesize(expr.operator.lexeme, expr.right);
    }

    private String parenthesize(String name, Expr... exprs) {
        StringBuilder builder = new StringBuilder();

        builder.append("(").append(name);
        for (Expr expr : exprs) {
            builder.append(" ");
            builder.append(expr.apply(this));
        }
        builder.append(")");

        return builder.toString();
    }

    public static void main(String[] args) {
        Expr expression = new Expr.Binary(
            new Expr.Unary(
                new Token(TokenType.MINUS, "-", null, 1),
                new Expr.Literal(123)),
            new Token(TokenType.STAR, "*", null, 1),
            new Expr.Grouping(
                new Expr.Literal(45.67)));

        System.out.println(new AstPrinter().print(expression));
    }
}
