import io.github.ibrhmkoz.monkeylang.token.Token;

static class Some {

    static Token token() {
        return new Token.Illegal('?');
        //        return Token.Plus.INSTANCE;
    }
}

void main() {
    var token = Some.token();

    switch (token) {
        case Token.Plus _ -> IO.println("");
        case Token.Illegal(var ch) -> IO.println(
                "This token is illegal: " + ch
        );
        default -> throw new IllegalStateException(
                "Unexpected value: " + token
        );
    }
}
