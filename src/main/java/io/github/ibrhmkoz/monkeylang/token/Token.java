package io.github.ibrhmkoz.monkeylang.token;

public sealed interface Token {
    record Ident(String name) implements Token {
    }

    record Int(int value) implements Token {
    }

    record Illegal(char ch) implements Token {
    }

    enum Assign implements Token {
        INSTANCE,
    }

    enum Plus implements Token {
        INSTANCE,
    }

    enum Minus implements Token {
        INSTANCE,
    }

    enum Bang implements Token {
        INSTANCE,
    }

    enum Asterisk implements Token {
        INSTANCE,
    }

    enum Slash implements Token {
        INSTANCE,
    }

    enum LessThan implements Token {
        INSTANCE,
    }

    enum GreaterThan implements Token {
        INSTANCE,
    }

    enum Comma implements Token {
        INSTANCE,
    }

    enum Semicolon implements Token {
        INSTANCE,
    }

    enum LParen implements Token {
        INSTANCE,
    }

    enum RParen implements Token {
        INSTANCE,
    }

    enum LBrace implements Token {
        INSTANCE,
    }

    enum RBrace implements Token {
        INSTANCE,
    }

    enum Function implements Token {
        INSTANCE,
    }

    enum Let implements Token {
        INSTANCE,
    }

    enum Eof implements Token {
        INSTANCE,
    }
}
