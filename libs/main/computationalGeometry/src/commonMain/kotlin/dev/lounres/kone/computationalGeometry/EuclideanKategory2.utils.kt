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


public data class EuclideanKategory2Scope<N, A, E: EuclideanKategory2<N>>(
    val numberRing: A,
    val euclideanKategory: E,
) where A: Ring<N>, A: Order<N>

public inline operator fun <N, A, E: EuclideanKategory2<N>,R> EuclideanKategory2Scope<N, A, E>.invoke(block: context(A, E) () -> R): R where A: Ring<N>, A: Order<N> {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.euclideanKategory)
}

public fun <N, A> A.euclideanKategory2(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory2<N> where A: Ring<N>, A: Order<N> =
    EuclideanKategory2WithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N, A> A.euclideanKategory2Scope(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory2Scope<N, A, EuclideanKategory2<N>> where A: Ring<N>, A: Order<N> =
    EuclideanKategory2Scope(this, euclideanKategory2(vectorKategory))

public inline fun <N, A, R> A.euclideanKategory2Scope(
    vectorKategory: VectorKategory<N> = vectorKategory(),
    block: context(A, EuclideanKategory2<N>) () -> R
): R where A: Ring<N>, A: Order<N> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this, this.euclideanKategory2(vectorKategory))
}