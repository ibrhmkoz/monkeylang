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
    }
}
