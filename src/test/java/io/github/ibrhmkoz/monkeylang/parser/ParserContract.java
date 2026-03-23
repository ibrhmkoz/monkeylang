package io.github.ibrhmkoz.monkeylang.parser;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ibrhmkoz.monkeylang.ast.Node;
import java.util.List;
import org.junit.jupiter.api.Test;

public interface ParserContract {

    Parser createParser(String input);

    @Test
    default void testLetStatementErrors() {
        var input =
                """
                let x 5;
                let = 10;
                let 838383;
                """;

        var parser = createParser(input);
        parser.parse();

        assertEquals(3, parser.errors().size());
    }

    @Test
    default void testReturnStatements() {
        var input =
                """
                return 5;
                return 10;
                return 993322;
                """;

        var program = createParser(input).parse();

        assertEquals(3, program.statements().size());
        assertTrue(program.statements().stream().allMatch(s -> s instanceof Node.Statement.Return));
    }

    @Test
    default void testLetStatements() {
        var input =
                """
                let x = 5;
                let y = 10;
                let foobar = 838383;
                """;

        var program = createParser(input).parse();

        assertEquals(3, program.statements().size());

        var expectedNames = List.of("x", "y", "foobar");
        for (int i = 0; i < 3; i++) {
            var let = (Node.Statement.Let) program.statements().get(i);
            assertEquals(expectedNames.get(i), let.name());
        }
    }
}
