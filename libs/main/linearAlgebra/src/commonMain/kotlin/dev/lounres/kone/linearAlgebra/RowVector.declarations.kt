/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import kotlinx.serialization.Serializable


@Serializable(with = RowVectorSerializer::class)
/*@JvmInline*/
public open /*value*/ class RowVector<out N, out Content: MDList1<N>>(
    public open val coefficients: Content,
) {
    public val size: UInt get() = coefficients.size[0u]
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "RowVector$coefficients"
    
    public companion object;
}

@Serializable(with = SettableRowVectorSerializer::class)
/*@JvmInline*/
public /*value*/ class SettableRowVector<N, out Content: SettableMDList1<N>>(
    override val coefficients: Content,
): RowVector<N, Content>(coefficients) {
    public operator fun set(index: UInt, coefficient: N) {
        coefficients[index] = coefficient
    }
}