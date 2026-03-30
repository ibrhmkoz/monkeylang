package io.github.ibrhmkoz.monkeylang.ast;

public enum InfixOp {
    ADD("+"),
    SUBTRACT("-"),
    MULTIPLY("*"),
    DIVIDE("/"),
    EQUAL("=="),
    NOT_EQUAL("!="),
    LESS_THAN("<"),
    GREATER_THAN(">");

    private final String symbol;

    InfixOp(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
