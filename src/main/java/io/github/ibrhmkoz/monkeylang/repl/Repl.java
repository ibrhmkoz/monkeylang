package io.github.ibrhmkoz.monkeylang.repl;

import io.github.ibrhmkoz.monkeylang.token.Token;
import io.github.ibrhmkoz.monkeylang.token.basic.BasicTokenizer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;

public class Repl {

    private static final String PROMPT = ">> ";

    public static void start(InputStream in, PrintStream out) throws IOException {
        var reader = new BufferedReader(new InputStreamReader(in));

        while (true) {
            out.print(PROMPT);
            var line = reader.readLine();
            if (line == null) {
                return;
            }

            var tokenizer = new BasicTokenizer(line);
            for (var tok = tokenizer.nextToken(); !(tok instanceof Token.Eof); tok = tokenizer.nextToken()) {
                out.println(tok);
            }
        }
    }
}
