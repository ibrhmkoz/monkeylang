package io.github.ibrhmkoz.monkeylang.token;

public sealed interface Token {
    record Ident(String name) implements Token {
    }

    record Int(int value) implements Token {
    }

    enum Operator implements Token {
        ASSIGN, PLUS,
    }

    enum Delimiter implements Token {
        COMMA, SEMICOLON, LPAREN, RPAREN, LBRACE, RBRACE
    }

    enum Keyword implements Token {
        FUNCTION, LET
    }

    enum EOF implements Token {
        INSTANCE;
    }

    record Illegal(char ch) implements Token {
    }
}

