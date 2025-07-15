/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.SettableMDList1
import kotlinx.serialization.Serializable


//@Serializable(with = ColumnVectorSerializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
/*@JvmInline*/
public open /*value*/ class ColumnVector<out N>(
    public open val coefficients: MDList1<N>
) {
    public val size: UInt get() = coefficients.size
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "ColumnVector$coefficients"
}

//@Serializable(with = SettableColumnVectorSerializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
/*@JvmInline*/
public /*value*/ class SettableColumnVector<N>(
    override val coefficients: SettableMDList1<N>
): ColumnVector<N>(coefficients) {
    public operator fun set(index: UInt, coefficient: N) {
        coefficients[index] = coefficient
    }
}