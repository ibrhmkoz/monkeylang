package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.ArrayList;

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
    public Node.Program parse() {
        var statements = new ArrayList<Node.Statement>();
        while (!(curToken instanceof Token.Eof)) {
            var stmt = parseStatement();
            if (stmt != null) {
                statements.add(stmt);
            }
            advance();
        }
        return new Node.Program(statements);
    }

    private Node.Statement parseStatement() {
        return switch (curToken) {
            case Token.Let _ -> parseLetStatement();
            default -> null;
        };
    }

    private Node.Statement.Let parseLetStatement() {
        if (!(peekToken instanceof Token.Ident(String name))) {
            return null;
        }
        advance();

        if (!(peekToken instanceof Token.Assign)) {
            return null;
        }

        do {
            advance();
        } while (!(curToken instanceof Token.Semicolon) && !(curToken instanceof Token.Eof));

        return new Node.Statement.Let(name, null);
    }

    private void advance() {
        curToken = peekToken;
        peekToken = tokenizer.nextToken();
    }
}
