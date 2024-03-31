/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMutableMap
import dev.lounres.kone.collections.getOrNull
import dev.lounres.kone.collections.utils.koneMutableMapOf
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.columnIndices
import dev.lounres.kone.multidimensionalCollections.experiment1.rowIndices
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class Matrix<N>(
    public open val coefficients: MDList2<N>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf(keyContext = defaultHashing())
): FeatureStorage {
    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFeature(key: Any, type: KClass<F>): F? = (defaultHashing<Any>()) { features.getOrNull(key)?.getOrNull(type) as? F }
    override fun <F : Any> storeFeature(key: Any, type: KClass<F>, value: F) {
        (defaultHashing<Any>()) {
            features.getOrSet(key) { koneMutableMapOf(keyContext = defaultHashing()) }[type] = value
        }
    }

    public val rowNumber: UInt get() = coefficients.rowNumber
    public val columnNumber: UInt get() = coefficients.columnNumber
    public operator fun get(rowIndex: UInt, columnIndex: UInt): N = coefficients[rowIndex, columnIndex]

    override fun toString(): String = "Matrix$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableMatrix<N>(
    override val coefficients: SettableMDList2<N>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf(keyContext = defaultHashing())
): Matrix<N>(coefficients, features) {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, coefficient: N) {
        features.removeAll()
        coefficients[rowIndex, columnIndex] = coefficient
    }
}

public fun <E> Matrix(vararg elements: KoneIterableList<E>, context: Equality<E>): Matrix<E> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct Matrix from list of list of different sizes" }
    return Matrix(MDList2(*elements, context = context))
}
public fun <E> Matrix(rowNumber: UInt, columnNumber: UInt, context: Equality<E>, initializer: (row: UInt, column: UInt) -> E): Matrix<E> =
    Matrix(MDList2(rowNumber, columnNumber, context = context, initializer))

public fun requireShapeEquality(left: Matrix<*>, right: Matrix<*>) {
    if (left.rowNumber != right.rowNumber || left.columnNumber != right.columnNumber)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val Matrix<*>.rowIndices: UIntRange get() = coefficients.rowIndices
public val Matrix<*>.columnIndices: UIntRange get() = coefficients.columnIndices