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


public data class EuclideanSpaceScope<N, A, V: VectorSpace<N>, E: EuclideanSpace<N>>(
    val numberRing: A,
    val vectorSpace: V,
    val euclideanSpace: E,
) where A: Ring<N>, A: Order<N>

public inline operator fun <N, A, V: VectorSpace<N>, E: EuclideanSpace<N>,R> EuclideanSpaceScope<N, A, V, E>.invoke(block: context(A, V, E) () -> R): R where A: Ring<N>, A: Order<N> {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.vectorSpace, this.euclideanSpace)
}

public val <N, A> A.euclideanSpace: EuclideanSpace<N> where A: Ring<N>, A: Order<N>
    get() = EuclideanSpaceWithNumberRingAndVectorSpace(this, this.vectorSpace)

public val <N, A> A.euclideanSpaceScope: EuclideanSpaceScope<N, A, VectorSpace<N>, EuclideanSpace<N>> where A: Ring<N>, A: Order<N>
    get() = EuclideanSpaceScope(this, this.vectorSpace, EuclideanSpaceWithNumberRingAndVectorSpace(this, this.vectorSpace))

public inline fun <N, A, R> A.euclideanSpace(
    block: context(A, VectorSpace<N>, EuclideanSpace<N>) () -> R
): R where A: Ring<N>, A: Order<N> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this, this.vectorSpace, this.euclideanSpace)
}