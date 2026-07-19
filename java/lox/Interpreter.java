package lox;

class Interpreter implements Expr.AstOperator<Object> {
    private AstPrinter astPrinter = new AstPrinter();

    void interpret(Expr expression) {
        try {
            Object value = evaluate(expression);
            System.out.println(stringify(value));
        } catch (RuntimeError error) {
            Lox.runtimeError(error);
        }
    }

    private Object evaluate(Expr expr) {
        return expr.apply(this);
    }

    @Override
    public Object onLiteralExpr(Expr.Literal expr) {
        return expr.value;
    }

    @Override
    public Object onGroupingExpr(Expr.Grouping expr) {
        return evaluate(expr.expression);
    }

    public Object onUnaryExpr(Expr.Unary expr) {
        Object right = evaluate(expr.right);
        switch (expr.operator.type) {
            case BANG:
                return !isTruthy(right);
            case MINUS:
                checkNumberOperand(expr.operator, right);
                return -(double)right;
            case null, default:
                throw new RuntimeError(expr.operator, "Unary operation not implemented.");
        }
    }

    public Object onBinaryExpr(Expr.Binary expr) {
        // IMPORTANT: evaluate operands in left to right order (side-effects)
        Object left = evaluate(expr.left);
        Object right = evaluate(expr.right);

        switch (expr.operator.type) {
            case GREATER:
                return checkedCompareTo(expr.operator, left, right) > 0;
            case GREATER_EQUAL:
                return checkedCompareTo(expr.operator, left, right) >= 0;
            case LESS:
                return checkedCompareTo(expr.operator, left, right) < 0;
            case LESS_EQUAL:
                return checkedCompareTo(expr.operator, left, right) <= 0;
            case BANG_EQUAL:
                return checkedCompareTo(expr.operator, left, right) != 0;
            case EQUAL_EQUAL:
                return checkedCompareTo(expr.operator, left, right) == 0;
            case MINUS:
                checkNumberOperands(expr.operator, left, right);
                return (double)left - (double)right;
            case PLUS:
                if (left instanceof Double && right instanceof Double) {
                    return (double)left + (double)right;
                }
                if (left instanceof String && right instanceof String) {
                    return (String)left + (String)right;
                }
                if (left instanceof String || right instanceof String) {
                    return stringify(left) + stringify(right);
                }
                throw new RuntimeError(expr.operator, "Operands must both be numbers or at least one operand must be a string.");
            case SLASH:
                checkNumberOperands(expr.operator, left, right);
                if ((double)right == 0) {
                    throw new RuntimeError(expr.operator, "Cannot divide by zero.");
                }
                return (double)left / (double)right;
            case STAR:
                checkNumberOperands(expr.operator, left, right);
                return (double)left * (double)right;
            case null, default:
                throw new RuntimeError(expr.operator, "Binary operation not implemented.");
        }
    }

    public Object onTernaryExpr(Expr.Ternary expr) {
        if (isTruthy(evaluate(expr.left))) {
            return evaluate(expr.middle);
        }
        return evaluate(expr.right);
    }

    private void checkNumberOperand(Token operator, Object operand) {
        if (operand instanceof Double) return;
        throw new RuntimeError(operator, "Operand must be a number.");
    }

    private void checkNumberOperands(Token operator, Object left, Object right) {
        if (left instanceof Double && right instanceof Double) return;
        throw new RuntimeError(operator, "Operands must be numbers.");
    }

    // TODO: make 0 falsey?
    private boolean isTruthy(Object object) {
        if (object == null) return false;
        if (object instanceof Boolean) return (boolean)object;
        return true;
    }

    // private boolean isEqual(Object a, Object b) {
    //     if (a == null && b == null) return true;
    //     if (a == null) return false;
    //     return a.equals(b);
    // }

    private int checkedCompareTo(Token operator, Object left, Object right) {
        if (left == null && right == null) return 0;
        if (left == null || right == null) throw new RuntimeError(operator, "Cannot compare nil with non-nil.");
        if (left.getClass() != right.getClass()) throw new RuntimeError(operator, "Cannot compare two values of different types.");
        if (left instanceof Double && right instanceof Double) {
            return ((Double)left).compareTo((Double)right);
        }
        if (left instanceof Boolean && right instanceof Boolean) {
            return ((Boolean)left).compareTo((Boolean)right);
        }
        if (left instanceof String && right instanceof String) {
            return ((String)left).compareTo((String)right);
        }
        throw new RuntimeError(operator, "Cannot compare operands.");
    }

    private String stringify(Object object) {
        if (object == null) return "nil";

        if (object instanceof Double) {
            String text = object.toString();
            if (text.endsWith(".0")) {
                text = text.substring(0, text.length() - 2);
            }
            return text;
        }

        return object.toString();
    }
}
