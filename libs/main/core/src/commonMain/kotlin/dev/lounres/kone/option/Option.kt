/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.option


/**
 * Container interface that is used to describe that there is either some value wrapped in the data class [Some]
 * or the [None] data object. It's a copy of `Option` from Rust stdlib.
 *
 * Why not just use `E?` instead of `Option<E>`? The reason is simple. If you `E` is nullable and you have possibility
 * of having either element of type `E` or a `null`, then you won't distinguish `null` as possible value and `null`
 * as absence of such value. That's why you should write `Option<E>` and you'll be able to distinguish `null` from `None`.
 */
public sealed interface Option<out E>

/**
 * Just a simple value container that is a part of [Option] purpose. See its docs for more.
 */
public data class Some<out E>(val value: E): Option<E>

/**
 * Just a simple marker of a value absence that is a part of [Option] purpose. See its docs for more.
 */
public data object None: Option<Nothing>

/**
 * Returns true if the value is present.
 */
public fun Option<*>.isSome(): Boolean =
    when(this) {
        None -> false
        is Some -> true
    }

/**
 * Returns true if the value is absent.
 */
public fun Option<*>.isNone(): Boolean = !isSome()

/**
 * Returns the value if it is present or throws the provided exception otherwise.
 */
public inline fun <E> Option<E>.orThrow(error: () -> Throwable): E =
    when(this) {
        None -> throw error()
        is Some -> value
    }

/**
 * Returns the value if it is present or [default] one otherwise.
 */
public fun <E> Option<E>.orDefault(default: E): E =
    when(this) {
        None -> default
        is Some -> value
    }

/**
 * Returns the value if it is present or computes and returns [default] one otherwise.
 */
public inline fun <E> Option<E>.orElse(default: () -> E): E =
    when(this) {
        None -> default()
        is Some -> value
    }

/**
 * Computes the [compute] on the value and returns it wrapped in [Some] if the value is present
 * or just returns `None` otherwise.
 */
public inline fun <E, R> Option<E>.computeOn(compute: (E) -> R): Option<R> =
    when(this) {
        None -> None
        is Some -> Some(compute(value))
    }

/**
 * Computes the [compute] on the value and returns it if the value is present
 * or just returns [default] one otherwise.
 */
public inline fun <E, R> Option<E>.computeOnOrDefault(default: R, compute: (E) -> R): R =
    when(this) {
        None -> default
        is Some -> compute(value)
    }

/**
 * Computes the [compute] on the value and returns it if the value is present
 * or just computes and returns [default] one otherwise.
 */
public inline fun <E, R> Option<E>.computeOnOrElse(default: () -> R, compute: (E) -> R): R =
    when(this) {
        None -> default()
        is Some -> compute(value)
    }

/**
 * Returns the wrapped in [Some] instance of type `Option<E>` if it is present or `None` otherwise.
 *
 * It's only purpose is to cease to distinguish `None` from `Some(None)` against the main meaning of `Option` interface.
 */
public fun <E> Option<Option<E>>.flatten(): Option<E> =
    when (this) {
        None -> None
        is Some -> value
    }