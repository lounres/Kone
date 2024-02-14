/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.linearAlgebra.experiment1.VectorSpace
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDListTransformer
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public inline operator fun <N, A, R> EuclideanSpace<N, A>.invoke(block: context(A, SettableMDListTransformer, VectorSpace<N, A>, EuclideanSpace<N, A>) () -> R): R where A: Ring<N>, A: Order<N> {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.vectorSpace.defaultSettableMdListTransformer, this.vectorSpace, this)
}

public val <N, A> A.euclideanSpace: EuclideanSpace<N, A> where A: Ring<N>, A: Order<N>
    get() = EuclideanSpace(this, VectorSpace(this, SettableMDListTransformer()))

public fun <N, A> A.euclideanSpace(
    mdListTransformer: SettableMDListTransformer = SettableMDListTransformer(),
): EuclideanSpace<N, A> where A: Ring<N>, A: Order<N> =
    EuclideanSpace(this, VectorSpace(this, mdListTransformer))

public inline fun <N, A, R> A.euclideanSpace(
    mdListTransformer: SettableMDListTransformer = SettableMDListTransformer(),
    block: context(A, SettableMDListTransformer, VectorSpace<N, A>, EuclideanSpace<N, A>) () -> R
): R where A: Ring<N>, A: Order<N> {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return EuclideanSpace(this, VectorSpace(this, mdListTransformer)).invoke(block)
}