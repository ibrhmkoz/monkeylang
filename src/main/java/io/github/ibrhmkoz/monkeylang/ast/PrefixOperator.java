package io.github.ibrhmkoz.monkeylang.ast;

public enum PrefixOperator {
    NEGATE("-"),
    NOT("!");

    private final String symbol;

    PrefixOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
