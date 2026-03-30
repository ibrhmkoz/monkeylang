package io.github.ibrhmkoz.lib.result;

public record Err<T, E>(E error) implements Result<T, E> {}
