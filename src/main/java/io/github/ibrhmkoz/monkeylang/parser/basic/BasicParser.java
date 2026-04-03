package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.lib.result.Err;
import io.github.ibrhmkoz.lib.result.Ok;
import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.lib.tracer.Tracer;
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
    private final Tracer tracer;
    private Token curToken;
    private Token peekToken;

    public BasicParser(Tokenizer tokenizer) {
        this(tokenizer, new Tracer.Noop());
    }

    public BasicParser(Tokenizer tokenizer, Tracer tracer) {
        this.tokenizer = tokenizer;
        this.tracer = tracer;
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
        tracer.enter("parseExpr", "bp=" + bp);

        var left = parsePrefix();

        while (left instanceof Ok<Node.Expr, String>(var expr)
            && peekToken instanceof Token.InfixOp op
            && bp < BindingPower.of(op).left()) {
            tracer.note("peek=" + op + " lbp=" + BindingPower.of(op).left() + " > bp=" + bp + " => bind");
            advance();
            left = parseInfix(expr);
        }

        if (peekToken instanceof Token.InfixOp op
            && left instanceof Ok<?, ?>
            && bp >= BindingPower.of(op).left()) {
            tracer.note("peek=" + op + " lbp=" + BindingPower.of(op).left() + " <= bp=" + bp + " => stop");
        }

        var result = formatResult(left);
        tracer.exit("parseExpr", result);
        return left;
    }

    private Result<Node.Expr, String> parsePrefix() {
        tracer.enter("parsePrefix", "");
        tracer.note("curToken=" + curToken);

        var result = switch (curToken) {
            case Token.Ident(var name) -> Result.<Node.Expr, String>ok(new Node.Expr.Ident(name));
            case Token.Int(var value) -> Result.<Node.Expr, String>ok(new Node.Expr.Int(value));
            case Token.Bool(var value) -> Result.<Node.Expr, String>ok(new Node.Expr.Bool(value));
            case Token.PrefixOp op -> parsePrefixExpr(op);
            case Token.LParen _ -> parseGroupedExpr();
            default -> Result.<Node.Expr, String>err("no prefix parser for: " + curToken);
        };

        tracer.exit("parsePrefix", formatResult(result));
        return result;
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
        tracer.enter("parseInfix", "left=" + left.unparse() + " op=" + op + " rbp=" + rbp);

        advance();
        var result = switch (parseExpr(rbp)) {
            case Ok<Node.Expr, String>(var right) -> Result.<Node.Expr, String>ok(new Node.Expr.Infix(left, op, right));
            case Err<Node.Expr, String> err -> err;
        };

        tracer.exit("parseInfix", formatResult(result));
        return result;
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

    private static String formatResult(Result<? extends Node, String> result) {
        return switch (result) {
            case Ok<? extends Node, String>(var node) -> node.unparse();
            case Err<? extends Node, String>(var error) -> "ERR: " + error;
        };
    }
}
