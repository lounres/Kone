/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.common.KoneMutableMap
import dev.lounres.kone.collections.common.getOrNull
import dev.lounres.kone.collections.common.utils.koneMutableMapOf
import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList2
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.columnIndices
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.rowIndices
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class Matrix<out N, in NE: Equality<@UnsafeVariance N>>(
    public open val coefficients: ContextualMDList2<N, NE>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): FeatureStorage {
    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFeature(key: Any, type: KClass<F>): F? = (defaultHashing<Any>()) { features.getOrNull(key)?.getOrNull(type) as? F }
    override fun <F : Any> storeFeature(key: Any, type: KClass<F>, value: F) {
        (defaultHashing<Any>()) {
            features.getOrSet(key) { koneMutableMapOf() }[type] = value
        }
    }

    public val rowNumber: UInt get() = coefficients.rowNumber
    public val columnNumber: UInt get() = coefficients.columnNumber
    public operator fun get(rowIndex: UInt, columnIndex: UInt): N = coefficients[rowIndex, columnIndex]

    override fun toString(): String = "Matrix$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableMatrix<N, in NE: Equality<N>>(
    override val coefficients: ContextualSettableMDList2<N, NE>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): Matrix<N, NE>(coefficients, features) {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, coefficient: N) {
        features.clear()
        coefficients[rowIndex, columnIndex] = coefficient
    }
}

public fun <E> Matrix(vararg elements: KoneContextualIterableList<E, *>): Matrix<E, Equality<E>> {
    require(elements.all { it.size == elements[0].size }) { "Cannot construct Matrix from list of list of different sizes" }
    return Matrix(ContextualMDList2(*elements))
}
public fun <E> Matrix(rowNumber: UInt, columnNumber: UInt, initializer: (row: UInt, column: UInt) -> E): Matrix<E, Equality<E>> =
    Matrix(ContextualMDList2(rowNumber, columnNumber, initializer))

public fun requireShapeEquality(left: Matrix<*, *>, right: Matrix<*, *>) {
    if (left.rowNumber != right.rowNumber || left.columnNumber != right.columnNumber)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val Matrix<*, *>.rowIndices: UIntRange get() = coefficients.rowIndices
public val Matrix<*, *>.columnIndices: UIntRange get() = coefficients.columnIndices