/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra

import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.SettableMDList2
import kotlinx.serialization.Serializable


//@Serializable(with = MatrixSerializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
/*@JvmInline*/
public open /*value*/ class Matrix<out N>(
    public open val coefficients: MDList2<N>
) {
    public val rowNumber: UInt get() = coefficients.rowNumber
    public val columnNumber: UInt get() = coefficients.columnNumber
    public operator fun get(rowIndex: UInt, columnIndex: UInt): N = coefficients[rowIndex, columnIndex]

    override fun toString(): String = "Matrix$coefficients"
}

//@Serializable(with = SettableMatrixSerializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
/*@JvmInline*/
public /*value*/ class SettableMatrix<N>(
    override val coefficients: SettableMDList2<N>
): Matrix<N>(coefficients) {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, coefficient: N) {
        coefficients[rowIndex, columnIndex] = coefficient
    }
}