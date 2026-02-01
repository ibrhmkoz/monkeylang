package io.github.ibrhmkoz.monkeylang.token.basic;

import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

public class BasicTokenizer implements Tokenizer {
    private final String input;
    private int i;


    public BasicTokenizer(String input) {
        this.input = input;
    }

    @Override
    public Token nextToken() {
        if (i == input.length()) {
            return Token.Eof.INSTANCE;
        }

        var ch = input.charAt(i);

        return switch (ch) {
            case '=' -> {
                i++;
                yield Token.Assign.INSTANCE;
            }
            case '+' -> {
                i++;
                yield Token.Plus.INSTANCE;
            }
            case ',' -> {
                i++;
                yield Token.Comma.INSTANCE;
            }
            case ';' -> {
                i++;
                yield Token.Semicolon.INSTANCE;
            }
            case '(' -> {
                i++;
                yield Token.LParen.INSTANCE;
            }
            case ')' -> {
                i++;
                yield Token.RParen.INSTANCE;
            }
            case '{' -> {
                i++;
                yield Token.LBrace.INSTANCE;
            }
            case '}' -> {
                i++;
                yield Token.RBrace.INSTANCE;
            }
            default -> {
                i++;
                yield new Token.Illegal(ch);
            }
        };
    }
}
