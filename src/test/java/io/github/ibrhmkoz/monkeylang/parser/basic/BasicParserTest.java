package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.parser.ParserContract;
import io.github.ibrhmkoz.monkeylang.token.basic.BasicTokenizer;

public class BasicParserTest implements ParserContract {

    @Override
    public Parser createParser(String input) {
        return new BasicParser(new BasicTokenizer(input));
    }
}
