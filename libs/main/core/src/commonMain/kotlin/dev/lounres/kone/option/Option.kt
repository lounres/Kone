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
 * as absence of such value. That's why you should write `Option<E>` and then you'll be able to distinguish `null` from `None`.
 */
public sealed interface Option<out Element>

/**
 * Just a simple value container that is a part of [Option] purpose. See its docs for more.
 */
public data class Some<out Element>(val value: Element): Option<Element>

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
public inline fun <Element> Option<Element>.orThrow(error: () -> Throwable): Element =
    when(this) {
        None -> throw error()
        is Some -> value
    }

/**
 * Returns the value if it is present or [default] one otherwise.
 */
public fun <Element> Option<Element>.orDefault(default: Element): Element =
    when(this) {
        None -> default
        is Some -> value
    }

/**
 * Returns the value if it is present or computes and returns [default] one otherwise.
 */
public inline fun <Element> Option<Element>.orElse(default: () -> Element): Element =
    when(this) {
        None -> default()
        is Some -> value
    }

/**
 * Computes the [compute] on the value and returns it wrapped in [Some] if the value is present
 * or just returns `None` otherwise.
 */
public inline fun <Element, Result> Option<Element>.computeOn(compute: (Element) -> Result): Option<Result> =
    when(this) {
        None -> None
        is Some -> Some(compute(value))
    }

/**
 * Computes the [compute] on the value and returns it if the value is present
 * or just returns [default] one otherwise.
 */
public inline fun <Element, Result> Option<Element>.computeOnOrDefault(default: Result, compute: (Element) -> Result): Result =
    when(this) {
        None -> default
        is Some -> compute(value)
    }

/**
 * Computes the [compute] on the value and returns it if the value is present
 * or just computes and returns [default] one otherwise.
 */
public inline fun <Element, Result> Option<Element>.computeOnOrElse(default: () -> Result, compute: (Element) -> Result): Result =
    when(this) {
        None -> default()
        is Some -> compute(value)
    }

/**
 * Wraps [this] value in [Some] if it is not null or returns [None] otherwise.
 */
public fun <Element: Any> Element?.notNullMaybe(): Option<Element> = if (this == null) None else Some(this)

/**
 * Transforms [this] value with [transform] function and wraps result in [Some] if [this] value is not null
 * or returns [None] otherwise.
 */
public fun <Element: Any, Result> Element?.transformNotNullMaybe(transform: (Element) -> Result): Option<Result> = if (this == null) None else Some(transform(this))

/**
 * Returns the wrapped in [Some] instance of type `Option<E>` if it is present or `None` otherwise.
 *
 * It's only purpose is to cease to distinguish `None` from `Some(None)` against the main meaning of `Option` interface.
 */
public fun <Element> Option<Option<Element>>.flatten(): Option<Element> =
    when (this) {
        None -> None
        is Some -> value
    }