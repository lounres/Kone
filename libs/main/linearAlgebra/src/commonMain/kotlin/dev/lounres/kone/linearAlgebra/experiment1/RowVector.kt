/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.common.KoneMutableMap
import dev.lounres.kone.collections.common.getOrNull
import dev.lounres.kone.collections.common.utils.koneMutableMapOf
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.ContextualSettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.indices
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class RowVector<out N, in NE: Equality<@UnsafeVariance N>>(
    public open val coefficients: ContextualMDList1<N, NE>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf() // TODO: Replace `Equality` with `Hashing`
): FeatureStorage {
    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFeature(key: Any, type: KClass<F>): F? = (defaultHashing<Any>()) { features.getOrNull(key)?.getOrNull(type) as? F }
    override fun <F : Any> storeFeature(key: Any, type: KClass<F>, value: F) {
        (defaultHashing<Any>()) {
            features.getOrSet(key) { koneMutableMapOf() }[type] = value
        }
    }

    public val size: UInt get() = coefficients.size
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "RowVector$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableRowVector<N, in NE: Equality<N>>(
    override val coefficients: ContextualSettableMDList1<N, NE>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): RowVector<N, NE>(coefficients, features) {
    public operator fun set(index: UInt, coefficient: N) {
        features.clear()
        coefficients[index] = coefficient
    }
}

public fun <E> RowVector(vararg elements: E): RowVector<E, Equality<E>> = RowVector(ContextualMDList1(*elements))
public fun <N> RowVector(size: UInt, initializer: (coefficient: UInt) -> N): RowVector<N, Equality<N>> = RowVector(ContextualMDList1(size, initializer))

public fun requireShapeEquality(left: RowVector<*, *>, right: RowVector<*, *>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val RowVector<*, *>.indices: UIntRange get() = coefficients.indices