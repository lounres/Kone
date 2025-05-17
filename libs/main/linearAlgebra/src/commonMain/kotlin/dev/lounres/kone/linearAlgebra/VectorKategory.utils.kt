/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1Producer
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2Producer
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDListProducer
import dev.lounres.kone.multidimensionalCollections.producers.*
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public fun <N> Ring<N>.vectorKategory(
    mdList1Producer: MDList1Producer = ArrayMDList1Producer,
    mdList2Producer: MDList2Producer = ArrayMDList2Producer,
): VectorKategory<N> = VectorKategoryWithNumberRing(this, mdList1Producer, mdList2Producer)

public fun <N> Ring<N>.vectorKategory(
    mdListProducer: MDListProducer = ArrayMDListProducer,
): VectorKategory<N> = VectorKategoryWithNumberRing(this, mdListProducer.as1D(), mdListProducer.as2D())

public fun <N> KoneContextRegistryBuilder.installVectorKategoryFor(
    numberType: SuppliedType,
    mdList1Producer: MDList1Producer = ArrayMDList1Producer,
    mdList2Producer: MDList2Producer = ArrayMDList2Producer,
) {
    val vectorKategory = contextsBuilder[Ring.Key<N>(numberType)].vectorKategory(mdList1Producer, mdList2Producer)
    contextsBuilder[VectorKategory.Key<N>(numberType)] = vectorKategory
}

public fun <N> KoneContextRegistryBuilder.installVectorKategoryFor(
    numberType: SuppliedType,
    mdListProducer: MDListProducer = ArrayMDListProducer,
) {
    val vectorKategory = contextsBuilder[Ring.Key<N>(numberType)].vectorKategory(mdListProducer)
    contextsBuilder[VectorKategory.Key<N>(numberType)] = vectorKategory
}

public fun <N, R> KoneContextRegistry.inVectorKategoryFor(numberType: SuppliedType, block: context(VectorKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[VectorKategory.Key<N>(numberType)])
}

public fun <N, R> KoneContextRegistry.inVectorKategoryScopeFor(numberType: SuppliedType, block: context(Ring<N>, VectorKategory<N>) () -> R): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return block(this.contexts[Ring.Key<N>(numberType)], this.contexts[VectorKategory.Key<N>(numberType)])
}