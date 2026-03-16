import io.github.ibrhmkoz.monkeylang.repl.Repl;

void main() throws Exception {
    var username = System.getProperty("user.name");
    IO.println("Hello %s! This is the Monkey programming language!".formatted(username));
    IO.println("Feel free to type in commands");
    Repl.start(System.in, System.out);
}
