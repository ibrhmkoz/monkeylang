package io.github.ibrhmkoz.monkeylang.ast;

import java.util.List;

public sealed interface Node {

    record Program(List<Statement> statements) implements Node {}

    sealed interface Statement extends Node {
        record Let(String name, Expression value) implements Statement {}

        record Return(Expression value) implements Statement {}

        record Expr(Expression expression) implements Statement {}
    }

    sealed interface Expression extends Node {
        record Identifier(String name) implements Expression {}
    }
}
