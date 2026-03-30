package io.github.ibrhmkoz.monkeylang.token;

public sealed interface Token {

    sealed interface InfixOp extends Token permits Plus, Minus, Asterisk, Slash, Eq, NotEq, LessThan, GreaterThan {}
    record Ident(String name) implements Token {}

    record Int(int value) implements Token {}

    record Bool(boolean value) implements Token {}

    record Illegal(char ch) implements Token {}

    enum Assign implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "=";
        }
    }

    enum Eq implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "==";
        }
    }

    enum NotEq implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "!=";
        }
    }

    enum Plus implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "+";
        }
    }

    enum Minus implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "-";
        }
    }

    enum Bang implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "!";
        }
    }

    enum Asterisk implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "*";
        }
    }

    enum Slash implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "/";
        }
    }

    enum LessThan implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return "<";
        }
    }

    enum GreaterThan implements InfixOp {
        INSTANCE;

        @Override
        public String toString() {
            return ">";
        }
    }

    enum Comma implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return ",";
        }
    }

    enum Semicolon implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return ";";
        }
    }

    enum LParen implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "(";
        }
    }

    enum RParen implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return ")";
        }
    }

    enum LBrace implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "{";
        }
    }

    enum RBrace implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "}";
        }
    }

    enum Function implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "fn";
        }
    }

    enum Let implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "let";
        }
    }


    enum If implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "if";
        }
    }

    enum Else implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "else";
        }
    }

    enum Return implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "return";
        }
    }

    enum Eof implements Token {
        INSTANCE;

        @Override
        public String toString() {
            return "EOF";
        }
    }
}
