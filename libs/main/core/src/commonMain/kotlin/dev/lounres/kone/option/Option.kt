/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.option


public sealed interface Option<out E>

public data class Some<out E>(val value: E): Option<E>
public data object None: Option<Nothing>

public fun <E> Option<E>.orDefault(default: E): E =
    when(this) {
        None -> default
        is Some -> value
    }

public inline fun <E> Option<E>.orElse(default: () -> E): E =
    when(this) {
        None -> default()
        is Some -> value
    }

public inline fun <E, R> Option<E>.computeOn(compute: (E) -> R): Option<R> =
    when(this) {
        None -> None
        is Some -> Some(compute(value))
    }

public inline fun <E, R> Option<E>.computeOnOrDefault(default: R, compute: (E) -> R): R =
    when(this) {
        None -> default
        is Some -> compute(value)
    }

public inline fun <E, R> Option<E>.computeOnOrElse(default: () -> R, compute: (E) -> R): R =
    when(this) {
        None -> default()
        is Some -> compute(value)
    }