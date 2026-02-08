/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.maybe

import kotlinx.serialization.Serializable


/**
 * Container interface that is used to describe that there is either some value wrapped in the data class [Some]
 * or the [None] data object.
 * It's a copy of [`Option`](https://doc.rust-lang.org/std/option/index.html) from Rust stdlib.
 *
 * Why not just use `E?` instead of `Maybe<E>`? The reason is simple. If you `E` is nullable and you have possibility
 * of having either element of type `E` or a `null`, then you won't distinguish `null` as possible value and `null`
 * as absence of such value. That's why you should write `Maybe<E>` and then you'll be able to distinguish `null` from `None`.
 */
@Serializable
public sealed interface Maybe<out Element>

/**
 * Just a simple value container that is a part of [Maybe] purpose. See its docs for more.
 */
@Serializable
public data class Some<out Element>(val value: Element): Maybe<Element>

/**
 * Just a simple marker of a value absence that is a part of [Maybe] purpose. See its docs for more.
 */
@Serializable
public data object None: Maybe<Nothing>

/**
 * Returns true if the value is present.
 */
public fun Maybe<*>.isSome(): Boolean =
    when(this) {
        None -> false
        is Some -> true
    }

/**
 * Returns true if the value is absent.
 */
public fun Maybe<*>.isNone(): Boolean = !isSome()

/**
 * Returns the value if it is present or throws the provided exception otherwise.
 */
public inline fun <Element> Maybe<Element>.orThrow(error: () -> Throwable): Element =
    when(this) {
        None -> throw error()
        is Some -> value
    }

/**
 * Returns the value if it is present or [default] one otherwise.
 */
public fun <Element> Maybe<Element>.orDefault(default: Element): Element =
    when(this) {
        None -> default
        is Some -> value
    }

/**
 * Returns the value if it is present or computes and returns [default] one otherwise.
 */
public inline fun <Element> Maybe<Element>.orElse(default: () -> Element): Element =
    when(this) {
        None -> default()
        is Some -> value
    }

/**
 * Computes the [compute] on the value and returns it wrapped in [Some] if the value is present
 * or just returns `None` otherwise.
 */
public inline fun <Element, Result> Maybe<Element>.map(compute: (Element) -> Result): Maybe<Result> =
    when(this) {
        None -> None
        is Some -> Some(compute(value))
    }

/**
 * Computes the [compute] on the value and returns it if the value is present
 * or just returns [default] result otherwise.
 */
public inline fun <Element, Result> Maybe<Element>.mapOrDefault(default: Result, compute: (Element) -> Result): Result =
    when(this) {
        None -> default
        is Some -> compute(value)
    }

/**
 * Computes the [compute] on the value and returns result of the computation if the value is present
 * or just computes and returns [default] result otherwise.
 */
public inline fun <Element, Result> Maybe<Element>.mapOrElse(default: () -> Result, compute: (Element) -> Result): Result =
    when(this) {
        None -> default()
        is Some -> compute(value)
    }

/**
 * Runs the [block] on the value if it is present.
 */
public inline fun <Element> Maybe<Element>.ifSome(block: (Element) -> Unit) {
    when (this) {
        None -> {}
        is Some<Element> -> block(this.value)
    }
}

/**
 * Wraps [this] value in [Some] if it is not null or returns [None] otherwise.
 */
public fun <Element: Any> Element?.notNullMaybe(): Maybe<Element> = if (this == null) None else Some(this)

/**
 * Transforms [this] value with [transform] function and wraps result in [Some] if [this] value is not null
 * or returns [None] otherwise.
 */
public fun <Element: Any, Result> Element?.transformNotNullMaybe(transform: (Element) -> Result): Maybe<Result> = if (this == null) None else Some(transform(this))

/**
 * Returns the wrapped in [Some] instance of type `Maybe<E>` if it is present or `None` otherwise.
 *
 * It's only purpose is to cease to distinguish `None` from `Some(None)` against the main meaning of `Maybe` interface.
 */
public fun <Element> Maybe<Maybe<Element>>.flatten(): Maybe<Element> =
    when (this) {
        None -> None
        is Some -> value
    }