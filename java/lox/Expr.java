package lox;

import java.util.List;

abstract class Expr {
    interface Operation<R> {
        R onBinaryExpr(Binary expr);
        R onTernaryExpr(Ternary expr);
        R onGroupingExpr(Grouping expr);
        R onLiteralExpr(Literal expr);
        R onUnaryExpr(Unary expr);
    }

    abstract <R> R apply(Operation<R> operation);

    static class Binary extends Expr {
        public Binary(Expr left, Token operator, Expr right) {
            this.left = left;
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R apply(Operation<R> operation) {
            return operation.onBinaryExpr(this);
        }

        final Expr left;
        final Token operator;
        final Expr right;
    }

    static class Ternary extends Expr {
        public Ternary(Expr left, Token leftOperator, Expr middle, Token rightOperator, Expr right) {
            this.left = left;
            this.leftOperator = leftOperator;
            this.middle = middle;
            this.rightOperator = rightOperator;
            this.right = right;
        }

        @Override
        <R> R apply(Operation<R> operation) {
            return operation.onTernaryExpr(this);
        }

        final Expr left;
        final Token leftOperator;
        final Expr middle;
        final Token rightOperator;
        final Expr right;
    }

    static class Grouping extends Expr {
        public Grouping(Expr expression) {
            this.expression = expression;
        }

        @Override
        <R> R apply(Operation<R> operation) {
            return operation.onGroupingExpr(this);
        }

        final Expr expression;
    }

    static class Literal extends Expr {
        public Literal(Object value) {
            this.value = value;
        }

        @Override
        <R> R apply(Operation<R> operation) {
            return operation.onLiteralExpr(this);
        }

        final Object value;
    }

    static class Unary extends Expr {
        public Unary(Token operator, Expr right) {
            this.operator = operator;
            this.right = right;
        }

        @Override
        <R> R apply(Operation<R> operation) {
            return operation.onUnaryExpr(this);
        }

        final Token operator;
        final Expr right;
    }
}
