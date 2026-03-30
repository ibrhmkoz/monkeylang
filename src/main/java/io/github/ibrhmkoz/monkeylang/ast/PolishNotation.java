package io.github.ibrhmkoz.monkeylang.ast;

public final class PolishNotation {

    private PolishNotation() {}

    public static String convert(Node.Expression expression) {
        return switch (expression) {
            case Node.Expression.Identifier(var name) -> name;
            case Node.Expression.IntegerLiteral(var value) -> Integer.toString(value);
            case Node.Expression.BooleanLiteral(var value) -> Boolean.toString(value);
            case Node.Expression.Prefix(var operator, var right) -> "(" + operator + " " + convert(right) + ")";
            case Node.Expression.Infix(var left, var operator, var right) -> "(" + operator + " " + convert(left) + " " + convert(right) + ")";
        };
    }
}
