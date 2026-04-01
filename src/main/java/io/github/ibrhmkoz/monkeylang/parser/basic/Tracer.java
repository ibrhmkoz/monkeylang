package io.github.ibrhmkoz.monkeylang.parser.basic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Tracer {

    void enter(String name, String args);

    void exit(String name, String result);

    void note(String message);

    class Noop implements Tracer {
        @Override
        public void enter(String name, String args) {}

        @Override
        public void exit(String name, String result) {}

        @Override
        public void note(String message) {}
    }

    class Printing implements Tracer {

        private int depth = 0;
        private final List<String> lines = new ArrayList<>();

        @Override
        public void enter(String name, String args) {
            lines.add(indent() + "> " + name + "(" + args + ")");
            depth++;
        }

        @Override
        public void exit(String name, String result) {
            depth--;
            lines.add(indent() + "< " + name + " = " + result);
        }

        @Override
        public void note(String message) {
            lines.add(indent() + "  " + message);
        }

        public List<String> lines() {
            return Collections.unmodifiableList(lines);
        }

        public String render() {
            return String.join("\n", lines);
        }

        private String indent() {
            return "  ".repeat(depth);
        }
    }
}
