package io.github.ibrhmkoz.monkeylang.ast;

public final class PolishNotation {

    private PolishNotation() {}

    public static String convert(Node.Expr expression) {
        return switch (expression) {
            case Node.Expr.Ident(var name) -> name;
            case Node.Expr.Int(var value) -> Integer.toString(value);
            case Node.Expr.Bool(var value) -> Boolean.toString(value);
            case Node.Expr.Prefix(var operator, var right) -> "(" + operator + " " + convert(right) + ")";
            case Node.Expr.Infix(var left, var operator, var right) -> "(" + operator + " " + convert(left) + " " + convert(right) + ")";
        };
    }
}
