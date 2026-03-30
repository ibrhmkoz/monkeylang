package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.lib.result.Err;
import io.github.ibrhmkoz.lib.result.Ok;
import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

record BindingPower(int left, int right) {

    static final int PREFIX_RIGHT = 10;

    static BindingPower of(Token.InfixOp op) {
        return switch (op) {
            case Token.Eq _, Token.NotEq _ -> new BindingPower(2, 3);
            case Token.LessThan _, Token.GreaterThan _ -> new BindingPower(4, 5);
            case Token.Plus _, Token.Minus _ -> new BindingPower(6, 7);
            case Token.Asterisk _, Token.Slash _ -> new BindingPower(8, 9);
        };
    }
}

public class BasicParser implements Parser {

    private final Tokenizer tokenizer;
    private Token curToken;
    private Token peekToken;

    public BasicParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        advance();
        advance();
    }

    @Override
    public Result<Node.Program, List<String>> parse() {
        var stmts = new ArrayList<Node.Stmt>();
        var errors = new ArrayList<String>();
        while (!(curToken instanceof Token.Eof)) {
            switch (parseStmt()) {
                case Ok<Node.Stmt, String>(var stmt) -> stmts.add(stmt);
                case Err<Node.Stmt, String>(var error) -> errors.add(error);
            }
            advance();
        }
        if (!errors.isEmpty()) {
            return Result.err(errors);
        }
        return Result.ok(new Node.Program(stmts));
    }

    private Result<Node.Stmt, String> parseStmt() {
        return switch (curToken) {
            case Token.Let _ -> parseLetStmt();
            case Token.Return _ -> parseReturnStmt();
            default -> parseExprStmt();
        };
    }

    private Result<Node.Stmt, String> parseExprStmt() {
        return switch (parseExpr(0)) {
            case Ok<Node.Expr, String>(var expr) -> {
                if (peekToken instanceof Token.Semicolon) {
                    advance();
                }
                yield Result.ok(new Node.Stmt.Expr(expr));
            }
            case Err<Node.Expr, String>(var error) -> Result.err(error);
        };
    }

    private Result<Node.Expr, String> parseExpr(int bp) {
        var left = parsePrefix();

        while (left instanceof Ok<Node.Expr, String>(var expr)
            && peekToken instanceof Token.InfixOp op
            && bp < BindingPower.of(op).left()) {
            advance();
            left = parseInfix(expr);
        }

        return left;
    }

    private Result<Node.Expr, String> parsePrefix() {
        return switch (curToken) {
            case Token.Ident(var name) -> Result.ok(new Node.Expr.Ident(name));
            case Token.Int(var value) -> Result.ok(new Node.Expr.Int(value));
            case Token.Bool(var value) -> Result.ok(new Node.Expr.Bool(value));
            case Token.PrefixOp op -> parsePrefixExpr(op);
            case Token.LParen _ -> parseGroupedExpr();
            default -> Result.err("no prefix parser for: " + curToken);
        };
    }

    private Result<Node.Expr, String> parsePrefixExpr(Token.PrefixOp op) {
        advance();
        return switch (parseExpr(BindingPower.PREFIX_RIGHT)) {
            case Ok<Node.Expr, String>(var right) -> Result.ok(new Node.Expr.Prefix(op, right));
            case Err<Node.Expr, String> err -> err;
        };
    }

    private Result<Node.Expr, String> parseGroupedExpr() {
        advance();
        var expr = parseExpr(0);
        if (!(peekToken instanceof Token.RParen)) {
            return Result.err("expected ), got: " + peekToken);
        }
        advance();
        return expr;
    }

    private Result<Node.Expr, String> parseInfix(Node.Expr left) {
        if (!(curToken instanceof Token.InfixOp op)) {
            throw new IllegalStateException("unexpected infix token: " + curToken);
        }
        var rbp = BindingPower.of(op).right();
        advance();
        return switch (parseExpr(rbp)) {
            case Ok<Node.Expr, String>(var right) -> Result.ok(new Node.Expr.Infix(left, op, right));
            case Err<Node.Expr, String> err -> err;
        };
    }

    private Result<Node.Stmt, String> parseLetStmt() {
        if (!(peekToken instanceof Token.Ident(String name))) {
            skipToSemicolon();
            return Result.err("expected identifier, got: " + peekToken);
        }
        advance();

        if (!(peekToken instanceof Token.Assign)) {
            skipToSemicolon();
            return Result.err("expected '=', got: " + peekToken);
        }

        advance();
        skipToSemicolon();

        return Result.ok(new Node.Stmt.Let(name, null));
    }

    private Result<Node.Stmt, String> parseReturnStmt() {
        advance();
        skipToSemicolon();
        return Result.ok(new Node.Stmt.Return(null));
    }

    private void skipToSemicolon() {
        while (!(curToken instanceof Token.Semicolon) && !(curToken instanceof Token.Eof)) {
            advance();
        }
    }

    private void advance() {
        curToken = peekToken;
        peekToken = tokenizer.nextToken();
    }
}
