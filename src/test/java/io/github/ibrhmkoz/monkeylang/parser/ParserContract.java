package io.github.ibrhmkoz.monkeylang.parser;

import static org.junit.jupiter.api.Assertions.*;

import io.github.ibrhmkoz.lib.result.Err;
import io.github.ibrhmkoz.lib.result.Ok;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import io.github.ibrhmkoz.monkeylang.ast.PolishNotation;

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
            case Err<Node.Program, List<String>>(var errors) -> assertEquals(3, errors.size());
            case Ok<Node.Program, List<String>> _ -> fail("expected errors");
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
            case Ok<Node.Program, List<String>>(var program) -> {
                assertEquals(3, program.stmts().size());
                assertTrue(program.stmts().stream().allMatch(s -> s instanceof Node.Stmt.Return));
            }
            case Err<Node.Program, List<String>> _ -> fail("expected success");
        }
    }

    @Test
    default void testUnparse() {
        var program = new Node.Program(List.of(
            new Node.Stmt.Let("myVar", new Node.Expr.Ident("anotherVar"))
        ));

        assertEquals("let myVar = anotherVar;", program.unparse());
    }

    @Test
    default void testExpressionStatement() {
        var input = "foobar;";

        var result = createParser(input).parse();

        switch (result) {
            case Ok<Node.Program, List<String>>(var program) -> {
                assertEquals(1, program.stmts().size());
                var stmt = (Node.Stmt.Expr) program.stmts().getFirst();
                var ident = (Node.Expr.Ident) stmt.expr();
                assertEquals("foobar", ident.name());
            }
            case Err<Node.Program, List<String>> _ -> fail("expected success");
        }
    }

    record Case(String input, String expected) {
        static Case given(String input) {
            return new Case(input, null);
        }

        Case expect(String expected) {
            return new Case(input, expected);
        }
    }

    @Test
    default void testOperatorPrecedenceParsing() {
        var cases = List.of(
            Case.given("-a * b").expect("(* (- a) b)"),
            Case.given("!-a").expect("(! (- a))"),
            Case.given("a + b + c").expect("(+ (+ a b) c)"),
            Case.given("a + b - c").expect("(- (+ a b) c)"),
            Case.given("a * b * c").expect("(* (* a b) c)"),
            Case.given("a * b / c").expect("(/ (* a b) c)"),
            Case.given("a + b / c").expect("(+ a (/ b c))"),
            Case.given("a + b * c + d / e - f").expect("(- (+ (+ a (* b c)) (/ d e)) f)"),
            Case.given("5 > 4 == 3 < 4").expect("(== (> 5 4) (< 3 4))"),
            Case.given("5 < 4 != 3 > 4").expect("(!= (< 5 4) (> 3 4))"),
            Case.given("3 + 4 * 5 == 3 * 1 + 4 * 5").expect("(== (+ 3 (* 4 5)) (+ (* 3 1) (* 4 5)))"),
            Case.given("true").expect("true"),
            Case.given("false").expect("false"),
            Case.given("3 > 5 == false").expect("(== (> 3 5) false)"),
            Case.given("3 < 5 == true").expect("(== (< 3 5) true)"),
            Case.given("1 + (2 + 3) + 4").expect("(+ (+ 1 (+ 2 3)) 4)"),
            Case.given("(5 + 5) * 2").expect("(* (+ 5 5) 2)"),
            Case.given("2 / (5 + 5)").expect("(/ 2 (+ 5 5))"),
            Case.given("-(5 + 5)").expect("(- (+ 5 5))"),
            Case.given("!(true == true)").expect("(! (== true true))")
        );

        for (var c : cases) {
            var result = createParser(c.input()).parse();

            switch (result) {
                case Ok<Node.Program, List<String>>(var program) -> {
                    var expr = ((Node.Stmt.Expr) program.stmts().getFirst()).expr();
                    assertEquals(c.expected(), PolishNotation.convert(expr));
                }
                case Err<Node.Program, List<String>>(var errors) ->
                    fail("expected success for '%s', got errors: %s".formatted(c.input(), errors));
            }
        }
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
            case Ok<Node.Program, List<String>>(var program) -> {
                assertEquals(3, program.stmts().size());

                var expectedNames = List.of("x", "y", "foobar");
                for (int i = 0; i < 3; i++) {
                    var let = (Node.Stmt.Let) program.stmts().get(i);
                    assertEquals(expectedNames.get(i), let.name());
                }
            }
            case Err<Node.Program, List<String>> _ -> fail("expected success");
        }
    }
}
