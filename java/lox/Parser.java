package lox;

import java.util.List;

class Parser {
    private static class ParseError extends RuntimeException {
        final Token token;
        final String message;

        ParseError(Token token, String message) {
            this.token = token;
            this.message = message;
        }
    }

    private final List<Token> tokens;
    private int current = 0;

    Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    Expr parse() {
        try {
            Expr expr = expression();
            consume(TokenType.EOF, "Expect end of expression.");
            return expr;
        } catch (ParseError error) {
            Lox.error(error.token, error.message);
            return null;
        }
    }

    @FunctionalInterface
    private interface ParseRule {
        Expr parse();
    }

    private Expr leftAssociative(ParseRule operand, TokenType... operators) {
        if (match(operators)) {
            Token op = previous();
            try {
                Expr ignored = operand.parse();
            } catch (ParseError ignored) {
                // Ignore any deeper errors
            }
            throw new ParseError(op, "Missing left operand before '" + op.lexeme + "'.");
        }
        Expr expr = operand.parse();
        while (match(operators)) {
            Token op = previous();
            Expr right = operand.parse();
            expr = new Expr.Binary(expr, op, right);
        }
        return expr;
    }

    private Expr expression() {
        return list();
    }

    // C's comma operator
    private Expr list() {
        return leftAssociative(this::conditional, TokenType.COMMA);
    }

    // Ternary operator
    private Expr conditional() {
        Expr expr = equality();
        if (match(TokenType.QUESTION)) {
            Token leftOperator = previous();
            Expr middle = expression();
            Token rightOperator = consume(TokenType.COLON, "Expect ':' after '?'.");
            Expr right = conditional();
            return new Expr.Ternary(expr, leftOperator, middle, rightOperator, right);
        }
        return expr;
    }

    private Expr equality() {
        return leftAssociative(this::comparison, TokenType.EQUAL_EQUAL, TokenType.BANG_EQUAL);
    }

    private Expr comparison() {
        return leftAssociative(this::sum, TokenType.GREATER_EQUAL, TokenType.GREATER, TokenType.LESS_EQUAL, TokenType.LESS);
    }

    private Expr sum() {
        return leftAssociative(this::product, TokenType.PLUS, TokenType.MINUS);
    }

    private Expr product() {
        return leftAssociative(this::unary, TokenType.STAR, TokenType.SLASH);
    }

    private Expr unary() {
        if (match(TokenType.BANG, TokenType.MINUS)) {
            Token operator = previous();
            Expr right = unary();
            return new Expr.Unary(operator, right);
        }
        return primary();
    }

    private Expr primary() {
        if (match(TokenType.FALSE)) return new Expr.Literal(false);
        if (match(TokenType.TRUE)) return new Expr.Literal(true);
        if (match(TokenType.NIL)) return new Expr.Literal(null);

        if (match(TokenType.NUMBER, TokenType.STRING)) {
            return new Expr.Literal(previous().literal);
        }

        if (match(TokenType.LEFT_PAREN)) {
            Expr expr = expression();
            consume(TokenType.RIGHT_PAREN, "Expect ')' after expression.");
            return new Expr.Grouping(expr);
        }

        throw new ParseError(peek(), "Expect expression.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) {
                advance();
                return true;
            }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new ParseError(peek(), message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() {
        return peek().type == TokenType.EOF;
    }

    private Token peek() {
        return tokens.get(current);
    }

    private Token previous() {
        return tokens.get(current - 1);
    }

    private void synchronize() {
        // Consume erroneous token
        advance();

        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;
            switch (peek().type) {
                case CLASS:
                case FUN:
                case VAR:
                case FOR:
                case IF:
                case WHILE:
                case PRINT:
                case RETURN:
                    // Reached start of new statement
                    return;
                case null, default:
                    advance();
                    break;
            }
        }
    }
}
