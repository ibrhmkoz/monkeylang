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
            !-/*5;
            5 < 10 > 5;
            if (5 < 10) {
                return true;
            } else {
                return false;
            }

            10 == 10;
            10 != 9;
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
            Token.Bang.INSTANCE,
            Token.Minus.INSTANCE,
            Token.Slash.INSTANCE,
            Token.Asterisk.INSTANCE,
            new Token.Int(5),
            Token.Semicolon.INSTANCE,
            new Token.Int(5),
            Token.LessThan.INSTANCE,
            new Token.Int(10),
            Token.GreaterThan.INSTANCE,
            new Token.Int(5),
            Token.Semicolon.INSTANCE,
            Token.If.INSTANCE,
            Token.LParen.INSTANCE,
            new Token.Int(5),
            Token.LessThan.INSTANCE,
            new Token.Int(10),
            Token.RParen.INSTANCE,
            Token.LBrace.INSTANCE,
            Token.Return.INSTANCE,
            Token.True.INSTANCE,
            Token.Semicolon.INSTANCE,
            Token.RBrace.INSTANCE,
            Token.Else.INSTANCE,
            Token.LBrace.INSTANCE,
            Token.Return.INSTANCE,
            Token.False.INSTANCE,
            Token.Semicolon.INSTANCE,
            Token.RBrace.INSTANCE,
            new Token.Int(10),
            Token.Eq.INSTANCE,
            new Token.Int(10),
            Token.Semicolon.INSTANCE,
            new Token.Int(10),
            Token.NotEq.INSTANCE,
            new Token.Int(9),
            Token.Semicolon.INSTANCE,
        };

        Tokenizer tokenizer = createTokenizer(input);
        for (var token : expected) {
            assertEquals(token, tokenizer.nextToken());
        }
        assertEquals(Token.Eof.INSTANCE, tokenizer.nextToken());
    }
}
