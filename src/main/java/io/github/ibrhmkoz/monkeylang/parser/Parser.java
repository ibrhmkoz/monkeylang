package io.github.ibrhmkoz.monkeylang.parser;

import io.github.ibrhmkoz.lib.result.Result;
import io.github.ibrhmkoz.monkeylang.ast.Node;
import java.util.List;

public interface Parser {
    Result<Node.Program, List<String>> parse();
}
