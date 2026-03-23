package io.github.ibrhmkoz.monkeylang.parser;

import io.github.ibrhmkoz.monkeylang.ast.Node;

public interface Parser {
    Node.Program parse();
}
