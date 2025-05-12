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


public fun <N> Ring<N>.euclideanKategory(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory<N> =
    EuclideanKategoryWithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N> KoneContextRegistryBuilder.installEuclideanKategoryFor(numberType: SuppliedType<N>) {
    val euclideanKategory = contextsBuilder[Ring.Key(numberType)].euclideanKategory(contextsBuilder[VectorKategory.Key(numberType)])
    contextsBuilder[EuclideanKategory.Key(numberType)] = euclideanKategory
}

public fun <N, R> KoneContextRegistry.inEuclideanKategoryFor(numberType: SuppliedType<N>, block: context(EuclideanKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[EuclideanKategory.Key(numberType)])
}

public fun <N, R> KoneContextRegistry.inEuclideanKategoryScopeFor(numberType: SuppliedType<N>, block: context(Ring<N>, Order<N>, EuclideanKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this.contexts[Ring.Key(numberType)],
        this.contexts[Order.Key(numberType)],
        this.contexts[EuclideanKategory.Key(numberType)]
    )
}