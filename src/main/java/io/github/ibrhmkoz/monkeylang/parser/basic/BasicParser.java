package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BasicParser implements Parser {

    private final Tokenizer tokenizer;
    private Token curToken;
    private Token peekToken;
    private final List<String> errors = new ArrayList<>();

    public BasicParser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
        advance();
        advance();
    }

    @Override
    public List<String> errors() {
        return Collections.unmodifiableList(errors);
    }

    @Override
    public Node.Program parse() {
        var statements = new ArrayList<Node.Statement>();
        while (!(curToken instanceof Token.Eof)) {
            switch (parseStatement()) {
                case Result.Ok<Node.Statement, String>(var stmt) -> statements.add(stmt);
                case Result.Err<Node.Statement, String>(var error) -> errors.add(error);
            }
            advance();
        }
        return new Node.Program(statements);
    }

    private Result<Node.Statement, String> parseStatement() {
        return switch (curToken) {
            case Token.Let _ -> parseLetStatement();
            case Token.Return _ -> parseReturnStatement();
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
