/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.context

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind.*
import kotlin.contracts.contract


/**
 * Marker interface that is used to mark specific classifiers that are made to be used like contexts.
 * They are usually provided together with functions like [invoke].
 *
 * The most common examples are `Equality` and `Order` from `kone.comparison` module or
 * `Ring` and `Field` from `kone.algebraic` module.
 */
public interface KoneContext

/**
 * Simple function that allows to elegantly use any [KoneContext] inheritor as a context receivers.
 */
@OptIn(ExperimentalContracts::class)
public inline operator fun <A: KoneContext, R> A.invoke(block: context(A) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this)
}