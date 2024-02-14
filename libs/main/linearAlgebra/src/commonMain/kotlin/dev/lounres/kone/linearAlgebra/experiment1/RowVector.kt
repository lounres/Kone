/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.standard.KoneMutableMap
import dev.lounres.kone.collections.standard.getOrNull
import dev.lounres.kone.collections.standard.utils.koneMutableMapOf
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.indices
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class RowVector<out N>(
    public open val coefficients: MDList1<N>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): FeatureStorage {
    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFeature(key: Any, type: KClass<F>): F? = features.getOrNull(key)?.getOrNull(type) as? F
    override fun <F : Any> storeFeature(key: Any, type: KClass<F>, value: F) {
        features.getOrSet(key) { koneMutableMapOf() } [type] = value
    }

    public val size: UInt get() = coefficients.size
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "RowVector$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableRowVector<N>(
    override val coefficients: SettableMDList1<N>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): RowVector<N>(coefficients, features) {
    public operator fun set(index: UInt, coefficient: N) {
        features.clear()
        coefficients[index] = coefficient
    }
}

public fun <E> RowVector(vararg elements: E): RowVector<E> = RowVector(MDList1(*elements))
public fun <N> RowVector(size: UInt, initializer: (coefficient: UInt) -> N): RowVector<N> = RowVector(MDList1(size, initializer))

public fun requireShapeEquality(left: RowVector<*>, right: RowVector<*>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val RowVector<*>.indices: UIntRange get() = coefficients.indices