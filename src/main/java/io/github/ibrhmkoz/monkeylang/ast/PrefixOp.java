package io.github.ibrhmkoz.monkeylang.ast;

public enum PrefixOp {
    NEGATE("-"),
    NOT("!");

    private final String symbol;

    PrefixOp(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
