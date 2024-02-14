/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.combinatorics.enumerative.extra

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.combinatorics.enumerative.cartesianProduct


public inline infix fun <E1, E2> KoneIterable<E1>.cartesianTimes(other: KoneIterable<E2>): Sequence<Pair<E1, E2>> =
    cartesianProduct(this, other)

public inline infix operator fun <E1, E2> KoneIterable<E1>.times(other: KoneIterable<E2>): Sequence<Pair<E1, E2>> =
    cartesianProduct(this, other)