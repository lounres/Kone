/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.linearAlgebra.experiment1.VectorKategory
import dev.lounres.kone.linearAlgebra.experiment1.vectorKategory
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class EuclideanKategoryScope<N, A, V: VectorKategory<N>, E: EuclideanKategory<N>>(
    val numberRing: A,
    val vectorKategory: V,
    val euclideanKategory: E,
) where A: Ring<N>, A: Order<N>

public inline operator fun <N, A, V: VectorKategory<N>, E: EuclideanKategory<N>,R> EuclideanKategoryScope<N, A, V, E>.invoke(block: context(A, V, E) () -> R): R where A: Ring<N>, A: Order<N> {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.vectorKategory, this.euclideanKategory)
}

public val <N, A> A.euclideanKategory: EuclideanKategory<N> where A: Ring<N>, A: Order<N>
    get() = EuclideanKategoryWithNumberRingAndVectorKategory(this, this.vectorKategory)

public val <N, A> A.euclideanKategoryScope: EuclideanKategoryScope<N, A, VectorKategory<N>, EuclideanKategory<N>> where A: Ring<N>, A: Order<N>
    get() = EuclideanKategoryScope(this, this.vectorKategory, EuclideanKategoryWithNumberRingAndVectorKategory(this, this.vectorKategory))

public inline fun <N, A, R> A.euclideanKategory(
    block: context(A, VectorKategory<N>, EuclideanKategory<N>) () -> R
): R where A: Ring<N>, A: Order<N> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this, this.vectorKategory, this.euclideanKategory)
}