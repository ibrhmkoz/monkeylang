package io.github.ibrhmkoz.monkeylang.parser;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ibrhmkoz.lib.result.Result;
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

        var result = createParser(input).parse();

        switch (result) {
            case Result.Err<Node.Program, List<String>>(var errors) -> assertEquals(3, errors.size());
            case Result.Ok<Node.Program, List<String>> _ -> fail("expected errors");
        }
    }

    @Test
    default void testReturnStatements() {
        var input =
                """
                return 5;
                return 10;
                return 993322;
                """;

        var result = createParser(input).parse();

        switch (result) {
            case Result.Ok<Node.Program, List<String>>(var program) -> {
                assertEquals(3, program.statements().size());
                assertTrue(program.statements().stream().allMatch(s -> s instanceof Node.Statement.Return));
            }
            case Result.Err<Node.Program, List<String>> _ -> fail("expected success");
        }
    }

    @Test
    default void testUnparse() {
        var program = new Node.Program(List.of(
                new Node.Statement.Let("myVar", new Node.Expression.Identifier("anotherVar"))
        ));

        assertEquals("let myVar = anotherVar;", program.unparse());
    }

    @Test
    default void testLetStatements() {
        var input =
                """
                let x = 5;
                let y = 10;
                let foobar = 838383;
                """;

        var result = createParser(input).parse();

        switch (result) {
            case Result.Ok<Node.Program, List<String>>(var program) -> {
                assertEquals(3, program.statements().size());

                var expectedNames = List.of("x", "y", "foobar");
                for (int i = 0; i < 3; i++) {
                    var let = (Node.Statement.Let) program.statements().get(i);
                    assertEquals(expectedNames.get(i), let.name());
                }
            }
            case Result.Err<Node.Program, List<String>> _ -> fail("expected success");
        }
    }
}
