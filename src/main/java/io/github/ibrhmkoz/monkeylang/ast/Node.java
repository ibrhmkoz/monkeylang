package io.github.ibrhmkoz.monkeylang.ast;

import java.util.List;

public sealed interface Node {

    record Program(List<Statement> statements) implements Node {}

    sealed interface Statement extends Node permits Statement.Let, Statement.Return, Statement.Expr {
        record Let(Expression.Identifier name, Expression value) implements Statement {}

        record Return(Expression value) implements Statement {}

        record Expr(Expression expression) implements Statement {}
    }

    sealed interface Expression extends Node permits Expression.Identifier {
        record Identifier(String name) implements Expression {}
    }
}
