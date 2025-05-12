/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.relations.Order
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.linearAlgebra.VectorKategory
import dev.lounres.kone.linearAlgebra.vectorKategory
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

public fun <N> KoneContextRegistryBuilder.installEuclideanKategory3For(numberType: SuppliedType<N>) {
    val euclideanKategory3 = contextsBuilder[Ring.Key(numberType)].euclideanKategory3(contextsBuilder[VectorKategory.Key(numberType)])
    contextsBuilder[EuclideanKategory3.Key(numberType)] = euclideanKategory3
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategory3For(numberType: SuppliedType<N>, block: context(EuclideanKategory3<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(contexts[EuclideanKategory3.Key(numberType)])
}

public inline fun <N, R> KoneContextRegistry.inEuclideanKategoryScope3For(numberType: SuppliedType<N>, block: context(Ring<N>, Order<N>, EuclideanKategory3<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        contexts[Ring.Key(numberType)],
        contexts[Order.Key(numberType)],
        contexts[EuclideanKategory3.Key(numberType)]
    )
}