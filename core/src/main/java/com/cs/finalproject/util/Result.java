package com.cs.finalproject.util;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Basically Rust's `Result<T, E>`.
 *
 * A Result represents either:
 * - Ok(T): an operation succeeded and produced a value
 * - Err(E): an operation failed and produced an error
 *
 * @param <T> the success value type
 * @param <E> the error type
 */
public final class Result<T, E> {
    private final T value;
    private final E error;
    private final boolean ok;

    private Result(T value, E error, boolean ok) {
        this.value = value;
        this.error = error;
        this.ok = ok;
    }

    public static <T, E> Result<T, E> ok(T value) {
        return new Result<>(Objects.requireNonNull(value), null, true);
    }

    public static <T, E> Result<T, E> err(E error) {
        return new Result<>(null, Objects.requireNonNull(error), false);
    }

    public boolean isOk() {
        return ok;
    }

    public boolean isErr() {
        return !ok;
    }

    /**
     * Returns the success value.
     *
     * @throws NoSuchElementException if this is Err
     */
    public T unwrap() {
        if (isErr()) {
            throw new NoSuchElementException(
                "called unwrap() on Err: " + error
            );
        }

        return value;
    }

    /**
     * Returns the error.
     *
     * @throws NoSuchElementException if this is Ok
     */
    public E unwrapErr() {
        if (isOk()) {
            throw new NoSuchElementException(
                "called unwrapErr() on Ok: " + value
            );
        }

        return error;
    }

    /**
     * Returns the success value, or a default value if Err.
     */
    public T unwrapOr(T defaultValue) {
        return isOk() ? value : defaultValue;
    }

    /**
     * Returns the success value, or computes a default if Err.
     */
    public T unwrapOrElse(Supplier<? extends T> supplier) {
        return isOk() ? value : supplier.get();
    }

    /**
     * Transforms the success value.
     *
     * Ok(T) -> Ok(R)
     * Err(E) -> Err(E)
     */
    public <R> Result<R, E> map(
        Function<? super T, ? extends R> mapper) {

        if (isErr()) {
            return Result.err(error);
        }

        return Result.ok(mapper.apply(value));
    }

    /**
     * Transforms the error.
     *
     * Ok(T) -> Ok(T)
     * Err(E) -> Err(F)
     */
    public <F> Result<T, F> mapErr(
        Function<? super E, ? extends F> mapper) {

        if (isOk()) {
            return Result.ok(value);
        }

        return Result.err(mapper.apply(error));
    }

    /**
     * Chains operations that themselves return Result.
     *
     * Ok(T) -> Result<R, E>
     * Err(E) -> Err(E)
     */
    public <R> Result<R, E> flatMap(
        Function<? super T, Result<R, E>> mapper) {

        if (isErr()) {
            return Result.err(error);
        }

        return Objects.requireNonNull(mapper.apply(value));
    }

    public void ifOk(Consumer<? super T> consumer) {
        if (isOk()) {
            consumer.accept(value);
        }
    }

    public void ifErr(Consumer<? super E> consumer) {
        if (isErr()) {
            consumer.accept(error);
        }
    }

    @Override
    public String toString() {
        return isOk()
            ? "Ok(" + value + ")"
            : "Err(" + error + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof Result<?, ?> other)) {
            return false;
        }

        return ok == other.ok
            && Objects.equals(value, other.value)
            && Objects.equals(error, other.error);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value, error, ok);
    }
}
