package io.github.ibrhmkoz.lib.result;

public record Ok<T, E>(T value) implements Result<T, E> {}
