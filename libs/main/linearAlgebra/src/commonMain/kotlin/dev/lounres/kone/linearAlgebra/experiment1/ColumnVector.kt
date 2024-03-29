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
import dev.lounres.kone.multidimensionalCollections.experiment1.complex.*
import kotlin.reflect.KClass


/*@JvmInline*/
public open /*value*/ class ColumnVector<N>(
    public open val coefficients: MDList1<N>,
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
public /*value*/ class SettableColumnVector<N>(
    override val coefficients: SettableMDList1<N>,
    override val features: KoneMutableMap<Any, KoneMutableMap<KClass<*>, Any>> = koneMutableMapOf()
): ColumnVector<N>(coefficients, features) {
    public operator fun set(index: UInt, coefficient: N) {
        features.clear()
        coefficients[index] = coefficient
    }
}

public fun <N> ColumnVector(vararg elements: N, context: Equality<N>): ColumnVector<N> = ColumnVector(MDList1(*elements, context = context))
context(Equality<N>)
public fun <N> ColumnVector(vararg elements: N): ColumnVector<N> = ColumnVector(MDList1(*elements, context = this@Equality))
public fun <N> ColumnVector(size: UInt, context: Equality<N>, initializer: (coefficient: UInt) -> N): ColumnVector<N> =
    ColumnVector(MDList1(size, context = context, initializer))
context(Equality<N>)
public fun <N> ColumnVector(size: UInt, initializer: (coefficient: UInt) -> N): ColumnVector<N> =
    ColumnVector(MDList1(size, context = this@Equality, initializer))

public fun requireShapeEquality(left: ColumnVector<*>, right: ColumnVector<*>) {
    if (left.size != right.size)
        throw ShapeMismatchException(left = left.coefficients.shape, right = right.coefficients.shape)
}

public val ColumnVector<*>.indices: UIntRange get() = coefficients.indices

internal class ColumnVectorEquality<N, NE: Equality<N>>(elementEquality: NE) : Equality<ColumnVector<N>> {
    val mdListEquality: Equality<MDList1<N>> = MDListEquality(elementEquality)
    override fun ColumnVector<N>.equalsTo(other: ColumnVector<N>): Boolean = mdListEquality { this.coefficients eq other.coefficients }
}

public fun <N, NE: Equality<N>> columnVectorEquality(elementEquality: NE): Equality<ColumnVector<N>> =
    ColumnVectorEquality(elementEquality)

internal class ColumnVectorHashing<N, NH: Hashing<N>>(elementHashing: NH) : Hashing<ColumnVector<N>> {
    val mdListHashing: Hashing<MDList1<N>> = MDListHashing(elementHashing)
    override fun ColumnVector<N>.equalsTo(other: ColumnVector<N>): Boolean = mdListHashing { this.coefficients eq other.coefficients }

    override fun ColumnVector<N>.hash(): Int = mdListHashing { this.coefficients.hash() }
}

public fun <N, NH: Hashing<N>> columnVectorHashing(elementHashing: NH): Hashing<ColumnVector<N>> =
    ColumnVectorHashing(elementHashing)