/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList1Producer
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDList2Producer
import dev.lounres.kone.multidimensionalCollections.implementations.ArrayMDListProducer
import dev.lounres.kone.multidimensionalCollections.producers.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


public data class VectorKategoryScope<N, A: Ring<N>, V: VectorKategory<N>>(val numberRing: A, val vectorSpace: V)

public inline operator fun <N, A: Ring<N>, V: VectorKategory<N>, R> VectorKategoryScope<N, A, V>.invoke(block: context(A, V) () -> R): R {
//    FIXME: KT-32313
//    contract {
//        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
//    }
    return block(this.numberRing, this.vectorSpace)
}

public fun <N> Ring<N>.vectorKategory(
    mdList1Producer: MDList1Producer = ArrayMDList1Producer,
    mdList2Producer: MDList2Producer = ArrayMDList2Producer,
): VectorKategory<N> = VectorKategoryWithNumberRing(this, mdList1Producer, mdList2Producer)

public fun <N> Ring<N>.vectorKategory(
    mdListProducer: MDListProducer = ArrayMDListProducer,
): VectorKategory<N> = VectorKategoryWithNumberRing(this, mdListProducer.as1D(), mdListProducer.as2D())

public fun <N, A: Ring<N>> A.vectorKategoryScope(
    mdList1Producer: MDList1Producer = ArrayMDList1Producer,
    mdList2Producer: MDList2Producer = ArrayMDList2Producer,
): VectorKategoryScope<N, A, VectorKategory<N>> =
    VectorKategoryScope(
        this,
        this.vectorKategory(
            mdList1Producer = mdList1Producer,
            mdList2Producer = mdList2Producer,
        )
    )

public fun <N, A: Ring<N>> A.vectorKategoryScope(
    mdListProducer: MDListProducer = ArrayMDListProducer,
): VectorKategoryScope<N, A, VectorKategory<N>> =
    VectorKategoryScope(
        this,
        this.vectorKategory(
            mdListProducer = mdListProducer,
        )
    )

public inline fun <N, A: Ring<N>, R> A.vectorKategoryScope(
    mdList1Producer: MDList1Producer = ArrayMDList1Producer,
    mdList2Producer: MDList2Producer = ArrayMDList2Producer,
    block: context(A, VectorKategory<N>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return this.vectorKategoryScope(mdList1Producer, mdList2Producer).invoke(block)
}

public inline fun <N, A: Ring<N>, R> A.vectorKategoryScope(
    mdListProducer: MDListProducer = ArrayMDListProducer,
    block: context(A, VectorKategory<N>) () -> R
): R {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return this.vectorKategoryScope(mdListProducer).invoke(block)
}