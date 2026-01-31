import io.github.ibrhmkoz.monkeylang.token.Token;

import static io.github.ibrhmkoz.monkeylang.token.Token.Operator.PLUS;

static class SomeFunction {
    static Token call() {
        return new Token.Illegal('?');
    }
}

void main() {
    var token = SomeFunction.call();

    switch (token) {
        case PLUS -> IO.println(token);
        case Token.Illegal(var ch) -> IO.println("This token is illegal: " + ch);
        default -> throw new IllegalStateException("Unexpected value: " + token);
    }
}
