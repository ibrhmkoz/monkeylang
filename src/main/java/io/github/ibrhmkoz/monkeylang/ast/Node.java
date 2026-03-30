package io.github.ibrhmkoz.monkeylang.ast;

import java.util.List;
import java.util.stream.Collectors;

public sealed interface Node {

    String unparse();

    record Program(List<Statement> statements) implements Node {
        @Override
        public String unparse() {
            return statements.stream().map(Statement::unparse).collect(Collectors.joining());
        }
    }

    sealed interface Statement extends Node {
        record Let(String name, Expression value) implements Statement {
            @Override
            public String unparse() {
                return "let " + name + " = " + value.unparse() + ";";
            }
        }

        record Return(Expression value) implements Statement {
            @Override
            public String unparse() {
                return "return " + value.unparse() + ";";
            }
        }

        record Expr(Expression expression) implements Statement {
            @Override
            public String unparse() {
                return expression.unparse();
            }
        }
    }

    sealed interface Expression extends Node {
        record Identifier(String name) implements Expression {
            @Override
            public String unparse() {
                return name;
            }
        }

        record IntegerLiteral(int value) implements Expression {
            @Override
            public String unparse() {
                return Integer.toString(value);
            }
        }

        record BooleanLiteral(boolean value) implements Expression {
            @Override
            public String unparse() {
                return Boolean.toString(value);
            }
        }

        enum PrefixOperator {
            NEGATE("-"),
            NOT("!");

            private final String symbol;

            PrefixOperator(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public String toString() {
                return symbol;
            }
        }

        enum InfixOperator {
            ADD("+"),
            SUBTRACT("-"),
            MULTIPLY("*"),
            DIVIDE("/"),
            EQUAL("=="),
            NOT_EQUAL("!="),
            LESS_THAN("<"),
            GREATER_THAN(">");

            private final String symbol;

            InfixOperator(String symbol) {
                this.symbol = symbol;
            }

            @Override
            public String toString() {
                return symbol;
            }
        }

        record Prefix(PrefixOperator operator, Expression right) implements Expression {
            @Override
            public String unparse() {
                return "(" + operator + right.unparse() + ")";
            }
        }

        record Infix(Expression left, InfixOperator operator, Expression right) implements Expression {
            @Override
            public String unparse() {
                return "(" + left.unparse() + " " + operator + " " + right.unparse() + ")";
            }
        }
    }
}
