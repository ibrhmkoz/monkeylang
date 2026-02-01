package io.github.ibrhmkoz.monkeylang.token;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public interface TokenizerContract {
    Tokenizer createTokenizer(String input);

    @Test
    default void testNextToken_BasicSymbols() {
        Tokenizer tokenizer = createTokenizer("=+(){},;");

        assertEquals(Token.Assign.INSTANCE, tokenizer.nextToken());
        assertEquals(Token.Plus.INSTANCE, tokenizer.nextToken());
    }

    @Test
    default void testNextToken_Eof() {
        Tokenizer tokenizer = createTokenizer("");
        assertEquals(Token.Eof.INSTANCE, tokenizer.nextToken());
    }

    @Test
    default void testNextToken_SimpleProgram() {
        var input = """
                let five = 5;
                let ten = 10;
                let add = fn(x, y) {
                    x + y;
                };
                let result = add(five, ten);
                """;

        var expected = new Token[]{
                Token.Let.INSTANCE,
                new Token.Ident("five"),
                Token.Assign.INSTANCE,
                new Token.Int(5),
                Token.Semicolon.INSTANCE,

                Token.Let.INSTANCE,
                new Token.Ident("ten"),
                Token.Assign.INSTANCE,
                new Token.Int(10),
                Token.Semicolon.INSTANCE,

                Token.Let.INSTANCE,
                new Token.Ident("add"),
                Token.Assign.INSTANCE,
                Token.Function.INSTANCE,
                Token.LParen.INSTANCE,
                new Token.Ident("x"),
                Token.Comma.INSTANCE,
                new Token.Ident("y"),
                Token.RParen.INSTANCE,
                Token.LBrace.INSTANCE,
                new Token.Ident("x"),
                Token.Plus.INSTANCE,
                new Token.Ident("y"),
                Token.Semicolon.INSTANCE,
                Token.RBrace.INSTANCE,
                Token.Semicolon.INSTANCE,

                Token.Let.INSTANCE,
                new Token.Ident("result"),
                Token.Assign.INSTANCE,
                new Token.Ident("add"),
                Token.LParen.INSTANCE,
                new Token.Ident("five"),
                Token.Comma.INSTANCE,
                new Token.Ident("ten"),
                Token.RParen.INSTANCE,
                Token.Semicolon.INSTANCE,
        };

        Tokenizer tokenizer = createTokenizer(input);
        for (Token token : expected) {
            assertEquals(token, tokenizer.nextToken());
        }
        assertEquals(Token.Eof.INSTANCE, tokenizer.nextToken());
    }
}