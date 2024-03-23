/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.linearAlgebra.experiment1

import dev.lounres.kone.collections.common.KoneMutableMap
import dev.lounres.kone.collections.common.getOrNull
import dev.lounres.kone.collections.common.utils.koneMutableMapOf
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.feature.FeatureStorage
import dev.lounres.kone.multidimensionalCollections.ShapeMismatchException
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.*
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class ColumnVector<out N, in NE: Equality<@UnsafeVariance N>>(
    public open val coefficients: ContextualMDList1<N, NE>,
    protected open val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
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

    override fun toString(): String = "ColumnVector$coefficients"
}
/*@JvmInline*/
public /*value*/ class SettableColumnVector<N, in NE: Equality<N>>(
    override val coefficients: ContextualSettableMDList1<N, NE>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): ColumnVector<N, NE>(coefficients, features) {
    public operator fun set(index: UInt, coefficient: N) {
        features.clear()
        coefficients[index] = coefficient
    }
}

public fun <N> ColumnVector(vararg elements: N): ColumnVector<N, Equality<N>> = ColumnVector(ContextualMDList1(*elements))
public fun <N> ColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): ColumnVector<N, Equality<N>> = ColumnVector(
    ContextualMDList1(size, initializer)
)

public fun requireShapeEquality(left: ColumnVector<*, *>, right: ColumnVector<*, *>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val ColumnVector<*, *>.indices: UIntRange get() = coefficients.indices

internal class ColumnVectorEquality<N, NE: Equality<N>>(elementEquality: NE) : Equality<ColumnVector<N, NE>> {
    val contextualMDListEquality: Equality<ContextualMDList1<N, NE>> = contextualMDListEquality(elementEquality)
    override fun ColumnVector<N, NE>.equalsTo(other: ColumnVector<N, NE>): Boolean = contextualMDListEquality.invoke { this.coefficients eq other.coefficients }
}

public fun <N, NE: Equality<N>> columnVectorEquality(elementEquality: NE): Equality<ColumnVector<N, NE>> =
    ColumnVectorEquality(elementEquality)

internal class ColumnVectorHashing<N, NH: Hashing<N>>(elementHashing: NH) : Hashing<ColumnVector<N, NH>> {
    val contextualMDListHashing: Hashing<ContextualMDList1<N, NH>> = contextualMDListHashing(elementHashing)
    override fun ColumnVector<N, NH>.equalsTo(other: ColumnVector<N, NH>): Boolean = contextualMDListHashing.invoke { this.coefficients eq other.coefficients }

    override fun ColumnVector<N, NH>.hash(): Int = contextualMDListHashing { this.coefficients.hash() }
}

public fun <N, NH: Hashing<N>> columnVectorHashing(elementHashing: NH): Hashing<ColumnVector<N, NH>> =
    ColumnVectorHashing(elementHashing)