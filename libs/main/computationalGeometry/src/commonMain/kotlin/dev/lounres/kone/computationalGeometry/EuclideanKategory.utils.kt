/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class EuclideanKategoryScope<N, A: Ring<N>, E: EuclideanKategory<N>>(
    val numberRing: A,
    val numberOrder: Order<N>,
    val euclideanKategory: E,
)

public inline operator fun <N, A: Ring<N>, E: EuclideanKategory<N>,R> EuclideanKategoryScope<N, A, E>.invoke(block: context(A, E) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.euclideanKategory)
}

public fun <N> Ring<N>.euclideanKategory(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory<N>  =
    EuclideanKategoryWithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N> KoneContextRegistryBuilder.installEuclideanKategoryFor(numberType: SuppliedType<N>) {
    val euclideanKategory = contextsBuilder[Ring.Key(numberType)].euclideanKategory(contextsBuilder[VectorKategory.Key(numberType)])
    contextsBuilder[EuclideanKategory.Key(numberType)] = euclideanKategory
}

context(koneContextRegistry: KoneContextRegistry)
public fun <N, R> inEuclideanKategoryFor(numberType: SuppliedType<N>, block: context(EuclideanKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(koneContextRegistry.contexts[EuclideanKategory.Key(numberType)])
}

context(koneContextRegistry: KoneContextRegistry)
public fun <N, R> inEuclideanKategoryScopeFor(numberType: SuppliedType<N>, block: context(Ring<N>, Order<N>, EuclideanKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        koneContextRegistry.contexts[Ring.Key(numberType)],
        koneContextRegistry.contexts[Order.Key(numberType)],
        koneContextRegistry.contexts[EuclideanKategory.Key(numberType)]
    )
}