package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.ArrayList;
import java.util.List;

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
        return switch (parseExpression()) {
            case Result.Ok<Node.Expression, String>(var expr) -> {
                if (peekToken instanceof Token.Semicolon) {
                    advance();
                }
                yield Result.ok(new Node.Statement.Expr(expr));
            }
            case Result.Err<Node.Expression, String>(var error) -> Result.err(error);
        };
    }

    private Result<Node.Expression, String> parseExpression() {
        return switch (curToken) {
            case Token.Ident(var name) -> Result.ok(new Node.Expression.Identifier(name));
            default -> Result.err("unexpected token: " + curToken);
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
