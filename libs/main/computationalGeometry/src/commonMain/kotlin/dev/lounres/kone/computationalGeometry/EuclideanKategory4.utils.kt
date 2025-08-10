/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.relations.Order
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class EuclideanKategory4Scope<N, A: Ring<N>, E: EuclideanKategory4<N>>(
    val numberRing: A,
    val numberOrder: Order<N>,
    val euclideanKategory: E,
)

public inline operator fun <N, A: Ring<N>, E: EuclideanKategory4<N>,R> EuclideanKategory4Scope<N, A, E>.invoke(block: context(A, E) () -> R): R {
//    FIXME: KT-44414
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.euclideanKategory)
}

public fun <N> Ring<N>.euclideanKategory4(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory4<N> =
    EuclideanKategory4WithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N> RegistryBuilder<KoneContextRegistry>.setEuclideanKategory4For(numberType: SuppliedType) {
    val euclideanKategory4 = this[Ring.Key<N>(numberType)].euclideanKategory4(this[VectorKategory.Key<N>(numberType)])
    this[EuclideanKategory4.Key<N>(numberType)] = euclideanKategory4
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategory4For(numberType: SuppliedType, block: context(EuclideanKategory4<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory4.Key<N>(numberType)])
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategoryScope4For(numberType: SuppliedType, block: context(Ring<N>, Order<N>, EuclideanKategory4<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key<N>(numberType)],
        contexts[Order.Key<N>(numberType)],
        contexts[EuclideanKategory4.Key<N>(numberType)]
    )
}