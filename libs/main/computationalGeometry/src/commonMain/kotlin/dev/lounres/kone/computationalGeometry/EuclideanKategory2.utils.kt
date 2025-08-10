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


public data class EuclideanKategory2Scope<N, A: Ring<N>, E: EuclideanKategory2<N>>(
    val numberRing: A,
    val numberOrder: Order<N>,
    val euclideanKategory: E,
)

public inline operator fun <N, A: Ring<N>, E: EuclideanKategory2<N>,R> EuclideanKategory2Scope<N, A, E>.invoke(block: context(A, E) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.euclideanKategory)
}

public fun <N> Ring<N>.euclideanKategory2(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory2<N> =
    EuclideanKategory2WithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N> RegistryBuilder<KoneContextRegistry>.setEuclideanKategory2For(numberType: SuppliedType) {
    val euclideanKategory2 = this[Ring.Key<N>(numberType)].euclideanKategory2(this[VectorKategory.Key<N>(numberType)])
    this[EuclideanKategory2.Key<N>(numberType)] = euclideanKategory2
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategory2For(numberType: SuppliedType, block: context(EuclideanKategory2<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory2.Key<N>(numberType)])
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategoryScope2For(numberType: SuppliedType, block: context(Ring<N>, Order<N>, EuclideanKategory2<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key(numberType)],
        contexts[Order.Key(numberType)],
        contexts[EuclideanKategory2.Key<N>(numberType)]
    )
}