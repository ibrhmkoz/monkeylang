package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

enum Precedence {
    LOWEST,
    EQUALS,
    LESSGREATER,
    SUM,
    PRODUCT,
    PREFIX,
    CALL;

    static Precedence of(Token token) {
        return switch (token) {
            case Token.Eq _, Token.NotEq _ -> EQUALS;
            case Token.LessThan _, Token.GreaterThan _ -> LESSGREATER;
            case Token.Plus _, Token.Minus _ -> SUM;
            case Token.Slash _, Token.Asterisk _ -> PRODUCT;
            default -> LOWEST;
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
        var statements = new ArrayList<Node.Statement>();
        var errors = new ArrayList<String>();
        while (!(curToken instanceof Token.Eof)) {
            switch (parseStatement()) {
                case Result.Ok<Node.Statement, String>(var stmt) -> statements.add(stmt);
                case Result.Err<Node.Statement, String>(var error) -> errors.add(error);
            }
            advance();
        }
        if (!errors.isEmpty()) {
            return Result.err(errors);
        }
        return Result.ok(new Node.Program(statements));
    }

    private Result<Node.Statement, String> parseStatement() {
        return switch (curToken) {
            case Token.Let _ -> parseLetStatement();
            case Token.Return _ -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
    }

    private Result<Node.Statement, String> parseExpressionStatement() {
        return switch (parseExpression(Precedence.LOWEST)) {
            case Result.Ok<Node.Expression, String>(var expr) -> {
                if (peekToken instanceof Token.Semicolon) {
                    advance();
                }
                yield Result.ok(new Node.Statement.Expr(expr));
            }
            case Result.Err<Node.Expression, String>(var error) -> Result.err(error);
        };
    }

    private Result<Node.Expression, String> parseExpression(Precedence precedence) {
        var left = parsePrefix();
        if (left instanceof Result.Err<Node.Expression, String>) {
            return left;
        }

        while (!(peekToken instanceof Token.Semicolon) && precedence.compareTo(Precedence.of(peekToken)) < 0) {
            if (!hasInfix(peekToken)) {
                return left;
            }
            advance();
            left = parseInfix(((Result.Ok<Node.Expression, String>) left).value());
        }

        return left;
    }

    private Result<Node.Expression, String> parsePrefix() {
        return switch (curToken) {
            case Token.Ident(var name) -> Result.ok(new Node.Expression.Identifier(name));
            case Token.Int(var value) -> Result.ok(new Node.Expression.IntegerLiteral(value));
            case Token.True _ -> Result.ok(new Node.Expression.BooleanLiteral(true));
            case Token.False _ -> Result.ok(new Node.Expression.BooleanLiteral(false));
            case Token.Bang _, Token.Minus _ -> parsePrefixExpression();
            case Token.LParen _ -> parseGroupedExpression();
            default -> Result.err("no prefix parser for: " + curToken);
        };
    }

    private Result<Node.Expression, String> parsePrefixExpression() {
        var operator = curToken.toString();
        advance();
        return switch (parseExpression(Precedence.PREFIX)) {
            case Result.Ok<Node.Expression, String>(var right) ->
                Result.ok(new Node.Expression.Prefix(operator, right));
            case Result.Err<Node.Expression, String> err -> err;
        };
    }

    private Result<Node.Expression, String> parseGroupedExpression() {
        advance();
        var expr = parseExpression(Precedence.LOWEST);
        if (!(peekToken instanceof Token.RParen)) {
            return Result.err("expected ), got: " + peekToken);
        }
        advance();
        return expr;
    }

    private boolean hasInfix(Token token) {
        return switch (token) {
            case Token.Plus _, Token.Minus _, Token.Slash _, Token.Asterisk _,
                 Token.Eq _, Token.NotEq _, Token.LessThan _, Token.GreaterThan _ -> true;
            default -> false;
        };
    }

    private Result<Node.Expression, String> parseInfix(Node.Expression left) {
        var operator = curToken.toString();
        var precedence = Precedence.of(curToken);
        advance();
        return switch (parseExpression(precedence)) {
            case Result.Ok<Node.Expression, String>(var right) ->
                Result.ok(new Node.Expression.Infix(left, operator, right));
            case Result.Err<Node.Expression, String> err -> err;
        };
    }

    private Result<Node.Statement, String> parseLetStatement() {
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

        return Result.ok(new Node.Statement.Let(name, null));
    }

    private Result<Node.Statement, String> parseReturnStatement() {
        advance();
        skipToSemicolon();
        return Result.ok(new Node.Statement.Return(null));
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
