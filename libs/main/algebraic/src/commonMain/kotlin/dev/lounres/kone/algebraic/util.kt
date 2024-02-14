/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.comparison.Order


context(A)
public fun <N, A> N.isPositive(): Boolean where A: Ring<N>, A: Order<N> = this > zero
context(A)
public fun <N, A> N.isNonPositive(): Boolean where A: Ring<N>, A: Order<N> = this <= zero
context(A)
public fun <N, A> N.isNegative(): Boolean where A: Ring<N>, A: Order<N> = this < zero
context(A)
public fun <N, A> N.isNonNegative(): Boolean where A: Ring<N>, A: Order<N> = this >= zero

context(A)
public val <N, A> N.sign: Int where A: Ring<N>, A: Order<N> get() {
    val compareResult = this.compareTo(zero)
    return when {
        compareResult > 0 -> 1
        compareResult < 0 -> -1
        else -> 0
    }
}