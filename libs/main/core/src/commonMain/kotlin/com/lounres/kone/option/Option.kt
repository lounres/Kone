/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.option

import com.lounres.kone.option.Option.*


public sealed interface Option<out T> {
    public data class Some<out T>(val value: T): Option<T>
    public data object None: Option<Nothing>
}

public fun <T> Option<T>.getOrDefault(defaultValue: T): T =
    when(this) {
        is Some -> value
        is None -> defaultValue
    }