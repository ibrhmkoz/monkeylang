package io.github.ibrhmkoz.monkeylang.parser.basic;

import io.github.ibrhmkoz.monkeylang.parser.Parser;
import io.github.ibrhmkoz.monkeylang.parser.ParserContract;
import io.github.ibrhmkoz.monkeylang.token.basic.BasicTokenizer;
import org.approvaltests.Approvals;
import org.junit.jupiter.api.Test;

public class TracedBasicParserTest implements ParserContract {

    @Override
    public Parser createParser(String input) {
        return new BasicParser(new BasicTokenizer(input), new Tracer.Printing());
    }

    private Tracer.Printing parseAndTrace(String input) {
        var tracer = new Tracer.Printing();
        var parser = new BasicParser(new BasicTokenizer(input), tracer);
        parser.parse();
        return tracer;
    }

    @Test
    void higherPrecedenceBindsFirst() {
        Approvals.verify(parseAndTrace("1 + 2 * 3").render());
    }

    @Test
    void lowerPrecedenceStops() {
        Approvals.verify(parseAndTrace("1 * 2 + 3").render());
    }

    @Test
    void leftAssociativity() {
        Approvals.verify(parseAndTrace("a + b + c").render());
    }

    @Test
    void mixedPrecedence() {
        Approvals.verify(parseAndTrace("a + b * c + d / e - f").render());
    }

    @Test
    void groupedExpression() {
        Approvals.verify(parseAndTrace("1 + (2 + 3) + 4").render());
    }

    @Test
    void prefixOperator() {
        Approvals.verify(parseAndTrace("-(5 + 5)").render());
    }

    @Test
    void mixedComparisonAndEquality() {
        Approvals.verify(parseAndTrace("5 > 4 == 3 < 4").render());
    }
}
