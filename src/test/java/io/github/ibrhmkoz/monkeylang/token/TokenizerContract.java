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
    default void testNextToken_EOF() {
        Tokenizer tokenizer = createTokenizer("");
        assertEquals(Token.Eof.INSTANCE, tokenizer.nextToken());
    }
}