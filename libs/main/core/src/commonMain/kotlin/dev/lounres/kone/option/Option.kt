/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.option


public sealed interface Option<out E> {
    public data class Some<out E>(val value: E): Option<E>
    public data object None: Option<Nothing>
}

public fun <E> Option<E>.getOrDefault(default: E): E =
    when(this) {
        Option.None -> default
        is Option.Some -> value
    }