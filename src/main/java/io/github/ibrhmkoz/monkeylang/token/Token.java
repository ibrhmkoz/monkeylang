package io.github.ibrhmkoz.monkeylang.token;

public sealed interface Token {
    record Ident(String name) implements Token {
    }

    record Int(int value) implements Token {
    }

    record Illegal(char ch) implements Token {
    }

    enum Simple implements Token {
        ASSIGN, PLUS, COMMA, SEMICOLON,
        LPAREN, RPAREN, LBRACE, RBRACE,
        FUNCTION, LET, EOF
    }
}