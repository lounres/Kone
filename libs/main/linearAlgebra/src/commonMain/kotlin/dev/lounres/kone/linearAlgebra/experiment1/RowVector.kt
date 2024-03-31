/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.KoneMutableMap
import dev.lounres.kone.collections.getOrNull
import dev.lounres.kone.collections.utils.koneMutableMapOf
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.MDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.SettableMDList1
import dev.lounres.kone.multidimensionalCollections.experiment1.indices
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class RowVector<N>(
    public open val coefficients: MDList1<N>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf(keyContext = defaultHashing())
): FeatureStorage {
    @Suppress("UNCHECKED_CAST")
    override fun <F : Any> getFeature(key: Any, type: KClass<F>): F? = (defaultHashing<Any>()) { features.getOrNull(key)?.getOrNull(type) as? F }
    override fun <F : Any> storeFeature(key: Any, type: KClass<F>, value: F) {
        (defaultHashing<Any>()) {
            features.getOrSet(key) { koneMutableMapOf(keyContext = defaultHashing()) } [type] = value
        }
    }

    public val size: UInt get() = coefficients.size
    public operator fun get(index: UInt): N = coefficients[index]

    override fun toString(): String = "RowVector$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableRowVector<N>(
    override val coefficients: SettableMDList1<N>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf(keyContext = defaultHashing())
): RowVector<N>(coefficients, features) {
    public operator fun set(index: UInt, coefficient: N) {
        features.removeAll()
        coefficients[index] = coefficient
    }
}

public fun <N> RowVector(vararg elements: N): RowVector<N> = RowVector(MDList1(*elements))
public fun <N> RowVector(size: UInt, initializer: (coefficient: UInt) -> N): RowVector<N> = RowVector(
    MDList1(size, initializer)
)

public fun requireShapeEquality(left: RowVector<*>, right: RowVector<*>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val RowVector<*>.indices: UIntRange get() = coefficients.indices