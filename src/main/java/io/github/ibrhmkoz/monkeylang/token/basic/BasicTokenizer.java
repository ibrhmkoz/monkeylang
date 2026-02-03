package io.github.ibrhmkoz.monkeylang.token.basic;

import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

import java.util.Map;

public class BasicTokenizer implements Tokenizer {

    private final String input;
    private int i = 0;

    private static final Map<Character, Token> SYMBOLS = Map.ofEntries(
            Map.entry('=', Token.Assign.INSTANCE),
            Map.entry('+', Token.Plus.INSTANCE),
            Map.entry('-', Token.Minus.INSTANCE),
            Map.entry('!', Token.Bang.INSTANCE),
            Map.entry('*', Token.Asterisk.INSTANCE),
            Map.entry('/', Token.Slash.INSTANCE),
            Map.entry('<', Token.LessThan.INSTANCE),
            Map.entry('>', Token.GreaterThan.INSTANCE),
            Map.entry('(', Token.LParen.INSTANCE),
            Map.entry(')', Token.RParen.INSTANCE),
            Map.entry('{', Token.LBrace.INSTANCE),
            Map.entry('}', Token.RBrace.INSTANCE),
            Map.entry(',', Token.Comma.INSTANCE),
            Map.entry(';', Token.Semicolon.INSTANCE)
    );

    private static final Map<String, Token> KEYWORDS = Map.of(
            "let",
            Token.Let.INSTANCE,
            "fn",
            Token.Function.INSTANCE
    );

    public BasicTokenizer(String input) {
        this.input = input;
    }

    @Override
    public Token nextToken() {
        skipWhitespace();

        if (i >= input.length()) {
            return Token.Eof.INSTANCE;
        }

        char ch = input.charAt(i);

        Token symbol = SYMBOLS.get(ch);
        if (symbol != null) {
            i++;
            return symbol;
        }

        if (isLetter(ch)) {
            return readIdentifier();
        }

        if (isDigit(ch)) {
            return readNumber();
        }

        i++;
        return new Token.Illegal(ch);
    }

    private Token readIdentifier() {
        int start = i;
        while (i < input.length() && isLetter(input.charAt(i))) {
            i++;
        }

        String word = input.substring(start, i);
        return KEYWORDS.getOrDefault(word, new Token.Ident(word));
    }

    private Token readNumber() {
        int start = i;
        while (i < input.length() && isDigit(input.charAt(i))) {
            i++;
        }

        String literal = input.substring(start, i);
        return new Token.Int(Integer.parseInt(literal));
    }

    private void skipWhitespace() {
        while (i < input.length() && Character.isWhitespace(input.charAt(i))) {
            i++;
        }
    }

    private boolean isLetter(char ch) {
        return (
                (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_'
        );
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
