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


public fun <N> Ring<N>.euclideanKategory(
    vectorKategory: VectorKategory<N> = vectorKategory(),
): EuclideanKategory<N> =
    EuclideanKategoryWithNumberRingAndVectorKategory(this, vectorKategory)

public fun <N> RegistryBuilder<KoneContextRegistry>.setEuclideanKategoryFor(numberType: SuppliedType) {
    val euclideanKategory = this[Ring.Key<N>(numberType)].euclideanKategory(this[VectorKategory.Key<N>(numberType)])
    this[EuclideanKategory.Key<N>(numberType)] = euclideanKategory
}

public fun <N, R> KoneContextRegistry.inEuclideanKategoryFor(numberType: SuppliedType, block: context(EuclideanKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[EuclideanKategory.Key<N>(numberType)])
}

public fun <N, R> KoneContextRegistry.inEuclideanKategoryScopeFor(numberType: SuppliedType, block: context(Ring<N>, Order<N>, EuclideanKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(
        this.contexts[Ring.Key(numberType)],
        this.contexts[Order.Key(numberType)],
        this.contexts[EuclideanKategory.Key<N>(numberType)]
    )
}