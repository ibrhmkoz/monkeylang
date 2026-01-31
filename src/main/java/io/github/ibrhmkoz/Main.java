import io.github.ibrhmkoz.monkeylang.token.Token;

import static io.github.ibrhmkoz.monkeylang.token.Token.Operator.PLUS;

static class Some {
    static Token token() {
        return new Token.Illegal('?');
    }
}

void main() {
    var token = Some.token();

    switch (token) {
        case PLUS -> IO.println(token);
        case Token.Illegal(var ch) -> IO.println("This token is illegal: " + ch);
        default -> throw new IllegalStateException("Unexpected value: " + token);
    }
}
