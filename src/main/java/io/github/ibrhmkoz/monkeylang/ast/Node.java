package io.github.ibrhmkoz.monkeylang.ast;

import io.github.ibrhmkoz.monkeylang.token.Token;

import java.util.List;
import java.util.stream.Collectors;

public sealed interface Node {

    String unparse();

    record Program(List<Stmt> stmts) implements Node {
        @Override
        public String unparse() {
            return stmts.stream().map(Stmt::unparse).collect(Collectors.joining());
        }
    }

    sealed interface Stmt extends Node {
        record Let(String name, Node.Expr value) implements Stmt {
            @Override
            public String unparse() {
                return "let " + name + " = " + value.unparse() + ";";
            }
        }

        record Return(Node.Expr value) implements Stmt {
            @Override
            public String unparse() {
                return "return " + value.unparse() + ";";
            }
        }

        record Expr(Node.Expr expr) implements Stmt {
            @Override
            public String unparse() {
                return expr.unparse();
            }
        }
    }

    sealed interface Expr extends Node {
        record Ident(String name) implements Expr {
            @Override
            public String unparse() {
                return name;
            }
        }

        record Int(int value) implements Expr {
            @Override
            public String unparse() {
                return Integer.toString(value);
            }
        }

        record Bool(boolean value) implements Expr {
            @Override
            public String unparse() {
                return Boolean.toString(value);
            }
        }

        record Prefix(Token.PrefixOp operator, Expr right) implements Expr {
            @Override
            public String unparse() {
                return "(" + operator + right.unparse() + ")";
            }
        }

        record Infix(Expr left, Token.InfixOp operator, Expr right) implements Expr {
            @Override
            public String unparse() {
                return "(" + left.unparse() + " " + operator + " " + right.unparse() + ")";
            }
        }
    }
}
