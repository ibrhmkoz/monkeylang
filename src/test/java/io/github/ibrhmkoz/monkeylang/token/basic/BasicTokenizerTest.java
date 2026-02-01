package io.github.ibrhmkoz.monkeylang.token.basic;

import io.github.ibrhmkoz.monkeylang.token.Tokenizer;
import io.github.ibrhmkoz.monkeylang.token.TokenizerContract;


class BasicTokenizerTest implements TokenizerContract {
    @Override
    public Tokenizer createTokenizer(String input) {
        return new BasicTokenizer(input);
    }
}