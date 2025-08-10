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


public data class EuclideanKategory3Scope<N, A: Ring<N>, E: EuclideanKategory3<N>>(
    val numberRing: A,
    val numberOrder: Order<N>,
    val euclideanKategory: E,
)

public inline operator fun <N, A: Ring<N>, E: EuclideanKategory3<N>,R> EuclideanKategory3Scope<N, A, E>.invoke(block: context(A, E) () -> R): R {
//    FIXME: KT-33313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.euclideanKategory)
}

public fun <N> Ring<N>.euclideanKategory3(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory3<N> =
    EuclideanKategory3WithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N> RegistryBuilder<KoneContextRegistry>.setEuclideanKategory3For(numberType: SuppliedType) {
    val euclideanKategory3 = this[Ring.Key<N>(numberType)].euclideanKategory3(this[VectorKategory.Key<N>(numberType)])
    this[EuclideanKategory3.Key<N>(numberType)] = euclideanKategory3
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategory3For(numberType: SuppliedType, block: context(EuclideanKategory3<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory3.Key<N>(numberType)])
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategoryScope3For(numberType: SuppliedType, block: context(Ring<N>, Order<N>, EuclideanKategory3<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key<N>(numberType)],
        contexts[Order.Key<N>(numberType)],
        contexts[EuclideanKategory3.Key<N>(numberType)]
    )
}