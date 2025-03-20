/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.relations.Order
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
import dev.lounres.kone.util.suppliedTypes.SuppliedType
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

public fun <N> KoneContextRegistryBuilder.installEuclideanKategory2For(numberType: SuppliedType<N>) {
    val euclideanKategory2 = contextsBuilder[Ring.Key(numberType)].euclideanKategory2(contextsBuilder[VectorKategory.Key(numberType)])
    contextsBuilder[EuclideanKategory2.Key(numberType)] = euclideanKategory2
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategory2For(numberType: SuppliedType<N>, block: context(EuclideanKategory2<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory2.Key(numberType)])
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategoryScope2For(numberType: SuppliedType<N>, block: context(Ring<N>, Order<N>, EuclideanKategory2<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key(numberType)],
        contexts[Order.Key(numberType)],
        contexts[EuclideanKategory2.Key(numberType)]
    )
}