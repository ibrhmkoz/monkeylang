package io.github.ibrhmkoz.monkeylang.parser;

import io.github.ibrhmkoz.monkeylang.ast.Node;
import java.util.List;

public interface Parser {
    Node.Program parse();

    List<String> errors();
}
