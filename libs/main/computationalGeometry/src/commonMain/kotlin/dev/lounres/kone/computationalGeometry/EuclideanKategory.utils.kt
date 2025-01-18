/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class EuclideanKategoryScope<N, A, E: EuclideanKategory<N>>(
    val numberRing: A,
    val euclideanKategory: E,
) where A: Ring<N>, A: Order<N>

public inline operator fun <N, A, E: EuclideanKategory<N>,R> EuclideanKategoryScope<N, A, E>.invoke(block: context(A, E) () -> R): R where A: Ring<N>, A: Order<N> {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.euclideanKategory)
}

public fun <N, A> A.euclideanKategory(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory<N> where A: Ring<N>, A: Order<N> =
    EuclideanKategoryWithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N, A> A.euclideanKategoryScope(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategoryScope<N, A, EuclideanKategory<N>> where A: Ring<N>, A: Order<N> =
    EuclideanKategoryScope(this, euclideanKategory(vectorKategory))

public inline fun <N, A, R> A.euclideanKategoryScope(
    vectorKategory: VectorKategory<N> = vectorKategory(),
    block: context(A, EuclideanKategory<N>) () -> R
): R where A: Ring<N>, A: Order<N> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this, this.euclideanKategory(vectorKategory))
}