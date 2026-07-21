/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.default2

import dev.lounres.kone.algebraic.*
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.computationalGeometry.Dot
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contexts.localUnwrap
import dev.lounres.kone.multidimensionalCollections.*
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline


@Serializable
@JvmInline
public value class Vector2<out N>(public val content: MDList1<N>) {
    init {
        require(content.size contentEquals MDSize.of(2u))
    }
    
    public val x: N get() = content[0u]
    public val y: N get() = content[1u]
    
    override fun toString(): String = content.toString()
}

public fun Vector2(x: Double, y: Double): Vector2<Double> {
    val contentArray = KoneDoubleArray.of(x, y)
    return Vector2(MDList1.generate(contentArray.size) { contentArray[it] })
}

@Serializable
@JvmInline
public value class Point2<out N>(public val content: MDList1<N>) {
    init {
        require(content.size contentEquals MDSize.of(2u))
    }
    
    public val x: N get() = content[0u]
    public val y: N get() = content[1u]
    
    override fun toString(): String = content.toString()
}

public fun <Number> Point2(x: Number, y: Number): Point2<Number> =
    Point2(
        MDList1.generate(2u) {
            when (it) {
                0u -> x
                1u -> y
                else -> TODO()
            }
        }
    )

public class EuclideanSpace2OverRing<Number>(
    private val ring: Ring<Number>,
) : EuclideanSpaceOverRing<Number, Vector2<Number>, Point2<Number>> {
    // region Constants
    override val zero: Vector2<Number> = Vector2(MDList1.generate(2u) { ring.zero })
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<Vector2<Number>> = IsZero {
        it.content.all { ring.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<Vector2<Number>, Int, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { ring.numberTimesInt { it * right } })
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<Vector2<Number>, UInt, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { ring.numberTimesUInt { it * right } })
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<Vector2<Number>, Long, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { ring.numberTimesLong { it * right } })
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<Vector2<Number>, ULong, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { ring.numberTimesULong { it * right } })
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<Vector2<Number>, Number, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { ring.numberTimesNumber { it * right } })
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { ring.intTimesNumber { left * it } })
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { ring.uIntTimesNumber { left * it } })
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { ring.longTimesNumber { left * it } })
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { ring.uLongTimesNumber { left * it } })
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { ring.numberTimesNumber { left * it } })
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<Vector2<Number>, Vector2<Number>> = UnaryMinus {
        Vector2(it.content.map { ring.numberUnaryMinus { -it } })
    }
    override val numberPlusNumber: Plus<Vector2<Number>, Vector2<Number>, Vector2<Number>> = Plus { left, right ->
        Vector2(MDList1.generate(2u) { ring.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val numberMinusNumber: Minus<Vector2<Number>, Vector2<Number>, Vector2<Number>> = Minus { left, right ->
        Vector2(MDList1.generate(2u) { ring.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    // endregion
    
    override val pointPlusVector: Plus<Point2<Number>, Vector2<Number>, Point2<Number>> = Plus { left, right ->
        Point2(MDList1.generate(2u) { ring.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val vectorPlusPoint: Plus<Vector2<Number>, Point2<Number>, Point2<Number>> = Plus { left, right ->
        Point2(MDList1.generate(2u) { ring.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val pointMinusVector: Minus<Point2<Number>, Vector2<Number>, Point2<Number>> = Minus { left, right ->
        Point2(MDList1.generate(2u) { ring.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    override val pointMinusPoint: Minus<Point2<Number>, Point2<Number>, Vector2<Number>> = Minus { left, right ->
        Vector2(MDList1.generate(2u) { ring.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    
    override val vectorDotVector: Dot<Vector2<Number>, Vector2<Number>, Number> = Dot { left, right ->
        KoneContext.localUnwrap(ring)
        (0u ..< 2u).toKoneList().sumOf { left.content[it] * right.content[it] }
    }
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<EuclideanSpace2OverRing<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpace2OverRing<Number>> by lazy {
            ImpliedKeysRegistry {
                EuclideanSpaceOverRing.Key<Number, Vector2<Number>, Point2<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverRing.Key<${suppliedTypeOf<Number>()}>"
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> EuclideanSpace2OverRing.Companion.setFor() {
    EuclideanSpace2OverRing.Key<Number>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        EuclideanSpace2OverRing(koneContextRegistry[Ring.Key<Number>()])
    }
}

public class EuclideanSpace2OverField<Number>(
    private val field: Field<Number>,
) : EuclideanSpaceOverField<Number, Vector2<Number>, Point2<Number>> {
    // region Constants
    override val zero: Vector2<Number> = Vector2(MDList1.generate(2u) { field.zero })
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<Vector2<Number>> = IsZero {
        it.content.all { field.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<Vector2<Number>, Int, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { field.numberTimesInt { it * right } })
    }
    override val vectorDivideInt: Divide<Vector2<Number>, Int, Vector2<Number>> = Divide { left, right ->
        Vector2(left.content.map { field.numberDivideInt { it / right } })
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<Vector2<Number>, UInt, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { field.numberTimesUInt { it * right } })
    }
    override val vectorDivideUInt: Divide<Vector2<Number>, UInt, Vector2<Number>> = Divide { left, right ->
        Vector2(left.content.map { field.numberDivideUInt { it / right } })
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<Vector2<Number>, Long, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { field.numberTimesLong { it * right } })
    }
    override val vectorDivideLong: Divide<Vector2<Number>, Long, Vector2<Number>> = Divide { left, right ->
        Vector2(left.content.map { field.numberDivideLong { it / right } })
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<Vector2<Number>, ULong, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { field.numberTimesULong { it * right } })
    }
    override val vectorDivideULong: Divide<Vector2<Number>, ULong, Vector2<Number>> = Divide { left, right ->
        Vector2(left.content.map { field.numberDivideULong { it / right } })
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<Vector2<Number>, Number, Vector2<Number>> = Times { left, right ->
        Vector2(left.content.map { field.numberTimesNumber { it * right } })
    }
    override val vectorDivideNumber: Divide<Vector2<Number>, Number, Vector2<Number>> = Divide { left, right ->
        Vector2(left.content.map { field.numberDivideNumber { it / right } })
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { field.intTimesNumber { left * it } })
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { field.uIntTimesNumber { left * it } })
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { field.longTimesNumber { left * it } })
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { field.uLongTimesNumber { left * it } })
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, Vector2<Number>, Vector2<Number>> = Times { left, right ->
        Vector2(right.content.map { field.numberTimesNumber { left * it } })
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<Vector2<Number>, Vector2<Number>> = UnaryMinus {
        Vector2(it.content.map { field.numberUnaryMinus { -it } })
    }
    override val numberPlusNumber: Plus<Vector2<Number>, Vector2<Number>, Vector2<Number>> = Plus { left, right ->
        Vector2(MDList1.generate(2u) { field.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val numberMinusNumber: Minus<Vector2<Number>, Vector2<Number>, Vector2<Number>> = Minus { left, right ->
        Vector2(MDList1.generate(2u) { field.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    // endregion
    
    override val pointPlusVector: Plus<Point2<Number>, Vector2<Number>, Point2<Number>> = Plus { left, right ->
        Point2(MDList1.generate(2u) { field.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val vectorPlusPoint: Plus<Vector2<Number>, Point2<Number>, Point2<Number>> = Plus { left, right ->
        Point2(MDList1.generate(2u) { field.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val pointMinusVector: Minus<Point2<Number>, Vector2<Number>, Point2<Number>> = Minus { left, right ->
        Point2(MDList1.generate(2u) { field.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    override val pointMinusPoint: Minus<Point2<Number>, Point2<Number>, Vector2<Number>> = Minus { left, right ->
        Vector2(MDList1.generate(2u) { field.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    
    override val vectorDotVector: Dot<Vector2<Number>, Vector2<Number>, Number> = Dot { left, right ->
        context(field, field.numberTimesNumber) { (0u ..< 2u).toKoneList().sumOf { left.content[it] * right.content[it] } }
    }
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<EuclideanSpace2OverField<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpace2OverField<Number>> by lazy {
            ImpliedKeysRegistry {
                EuclideanSpaceOverField.Key<Number, Vector2<Number>, Point2<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField.Key<${suppliedTypeOf<Number>()}>"
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> EuclideanSpace2OverField.Companion.setFor() {
    EuclideanSpace2OverField.Key<Number>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        EuclideanSpace2OverField(koneContextRegistry[Field.Key<Number>()])
    }
}