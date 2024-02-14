/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.standard.*
import dev.lounres.kone.collections.standard.utils.koneMutableMapOf
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.*
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class Matrix<out N>(
    public open val coefficients: MDList2<N>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): FeatureStorage {
    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFeature(key: Any, type: KClass<F>): F? = features.getOrNull(key)?.getOrNull(type) as? F
    override fun <F : Any> storeFeature(key: Any, type: KClass<F>, value: F) {
        features.getOrSet(key) { koneMutableMapOf() } [type] = value
    }

    public val rowNumber: UInt get() = coefficients.rowNumber
    public val columnNumber: UInt get() = coefficients.columnNumber
    public operator fun get(rowIndex: UInt, columnIndex: UInt): N = coefficients[rowIndex, columnIndex]

    override fun toString(): String = "Matrix$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableMatrix<N>(
    override val coefficients: SettableMDList2<N>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): Matrix<N>(coefficients, features) {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, coefficient: N) {
        features.clear()
        coefficients[rowIndex, columnIndex] = coefficient
    }
}

public fun <E> Matrix(vararg elements: KoneIterableList<E>): Matrix<E> {
    require(elements.all { it.size == elements[0].size }) { TODO() }
    return Matrix(MDList2(*elements))
}
public fun <E> Matrix(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): Matrix<E> =
    Matrix(MDList2(rowNumber, columnNumber, initializer))

public fun requireShapeEquality(left: Matrix<*>, right: Matrix<*>) {
    if (left.rowNumber != right.rowNumber || left.columnNumber != right.columnNumber)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val Matrix<*>.rowIndices: UIntRange get() = coefficients.rowIndices
public val Matrix<*>.columnIndices: UIntRange get() = coefficients.columnIndices