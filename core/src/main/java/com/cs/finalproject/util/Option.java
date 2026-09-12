package com.cs.finalproject.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Basically re-implementing Rust's `Option<T>`.
 *
 * Option<T> represents either:
 * - Some(T): a value exists
 * - None: no value exists
 *
 * @param <T> the contained value type
 */
public final class Option<T> {
    private final T value;

    private Option(T value) {
        this.value = value;
    }

    public static <T> Option<T> some(T value) {
        return new Option<>(Objects.requireNonNull(value));
    }

    public static <T> Option<T> none() {
        return new Option<>(null);
    }

    public static <T> Option<T> of(T value) {
        return value == null ? none() : some(value);
    }

    public boolean isSome() {
        return value != null;
    }

    public boolean isNone() {
        return value == null;
    }

    /**
     * Returns the contained value.
     *
     * @throws NoSuchElementException if this is None
     */
    public T unwrap() {
        if (isNone()) {
            throw new NoSuchElementException("called unwrap() on None");
        }

        return value;
    }

    /**
     * Returns the contained value, or a default value.
     */
    public T unwrapOr(T defaultValue) {
        return isSome() ? value : defaultValue;
    }

    /**
     * Returns the contained value, or computes a default.
     */
    public T unwrapOrElse(Supplier<? extends T> supplier) {
        return isSome() ? value : supplier.get();
    }

    /**
     * Transforms Some(T) into Some(R).
     * None remains None.
     */
    public <R> Option<R> map(Function<? super T, ? extends R> mapper) {
        if (isNone()) {
            return Option.none();
        }

        return Option.of(mapper.apply(value));
    }

    /**
     * Like map(), but the mapper itself returns an Option.
     */
    public <R> Option<R> flatMap(
        Function<? super T, Option<R>> mapper) {

        if (isNone()) {
            return Option.none();
        }

        return Objects.requireNonNull(mapper.apply(value));
    }

    public void ifSome(Consumer<? super T> consumer) {
        if (isSome()) {
            consumer.accept(value);
        }
    }

    @Override
    public String toString() {
        return isSome()
            ? "Some(" + value + ")"
            : "None";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Option<?> other)) {
            return false;
        }

        return Objects.equals(value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
