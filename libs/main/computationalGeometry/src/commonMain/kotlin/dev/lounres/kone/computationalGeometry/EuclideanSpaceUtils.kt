/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.linearAlgebra.experiment1.VectorSpace
import dev.lounres.kone.linearAlgebra.experiment1.vectorSpace
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public inline operator fun <N, A, R> EuclideanSpace<N, A>.invoke(block: context(A, VectorSpace<N>, EuclideanSpace<N, A>) () -> R): R where A: Ring<N>, A: Order<N> {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.vectorSpace, this)
}

public val <N, A> A.euclideanSpace: EuclideanSpace<N, A> where A: Ring<N>, A: Order<N>
    get() = EuclideanSpace(this, this.vectorSpace)

public inline fun <N, A, R> A.euclideanSpace(
    block: context(A, VectorSpace<N>, EuclideanSpace<N, A>) () -> R
): R where A: Ring<N>, A: Order<N> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val euclideanSpace = EuclideanSpace(this, this.vectorSpace)
    return block(euclideanSpace.numberRing, euclideanSpace.vectorSpace, euclideanSpace)
}