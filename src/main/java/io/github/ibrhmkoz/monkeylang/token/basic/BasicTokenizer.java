package io.github.ibrhmkoz.monkeylang.token.basic;

import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.Tokenizer;

public class BasicTokenizer implements Tokenizer {
    public BasicTokenizer(String input) {
    }

    @Override
    public Token nextToken() {
        return Token.Eof.INSTANCE;
    }
}
