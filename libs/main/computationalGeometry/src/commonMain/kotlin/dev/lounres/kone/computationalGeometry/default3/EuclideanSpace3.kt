/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.default3

import dev.lounres.kone.algebraic.*
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
import dev.lounres.kone.contexts.unwrap
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
public value class Vector3<out N>(public val content: MDList1<N>) {
    init {
        require(content.size contentEquals MDSize.of(3u))
    }
    
    public val x: N get() = content[0u]
    public val y: N get() = content[1u]
    public val z: N get() = content[2u]
    
    override fun toString(): String = content.toString()
}

public fun Vector3(x: Double, y: Double, z: Double): Vector3<Double> {
    val contentArray = KoneDoubleArray.of(x, y, z)
    return Vector3(MDList1.generate(contentArray.size) { contentArray[it] })
}

@Serializable
@JvmInline
public value class Point3<out N>(public val content: MDList1<N>) {
    init {
        require(content.size contentEquals MDSize.of(3u))
    }
    
    public val x: N get() = content[0u]
    public val y: N get() = content[1u]
    public val z: N get() = content[2u]
    
    override fun toString(): String = content.toString()
}

public fun <Number> Point3(x: Number, y: Number, z: Number): Point3<Number> =
    Point3(
        MDList1.generate(3u) {
            when (it) {
                0u -> x
                1u -> y
                2u -> z
                else -> TODO()
            }
        }
    )

public class EuclideanSpace3OverRing<Number>(
    private val ring: Ring<Number>,
) : EuclideanSpaceOverRing<Number, Vector3<Number>, Point3<Number>> {
    // region Constants
    override val zero: Vector3<Number> = Vector3(MDList1.generate(3u) { ring.zero })
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<Vector3<Number>> = IsZero {
        it.content.all { ring.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<Vector3<Number>, Int, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { ring.numberTimesInt { it * right } })
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<Vector3<Number>, UInt, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { ring.numberTimesUInt { it * right } })
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<Vector3<Number>, Long, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { ring.numberTimesLong { it * right } })
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<Vector3<Number>, ULong, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { ring.numberTimesULong { it * right } })
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<Vector3<Number>, Number, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { ring.numberTimesNumber { it * right } })
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { ring.intTimesNumber { left * it } })
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { ring.uIntTimesNumber { left * it } })
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { ring.longTimesNumber { left * it } })
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { ring.uLongTimesNumber { left * it } })
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { ring.numberTimesNumber { left * it } })
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<Vector3<Number>, Vector3<Number>> = UnaryMinus {
        Vector3(it.content.map { ring.numberUnaryMinus { -it } })
    }
    override val numberPlusNumber: Plus<Vector3<Number>, Vector3<Number>, Vector3<Number>> = Plus { left, right ->
        Vector3(MDList1.generate(3u) { ring.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val numberMinusNumber: Minus<Vector3<Number>, Vector3<Number>, Vector3<Number>> = Minus { left, right ->
        Vector3(MDList1.generate(3u) { ring.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    // endregion
    
    override val pointPlusVector: Plus<Point3<Number>, Vector3<Number>, Point3<Number>> = Plus { left, right ->
        Point3(MDList1.generate(3u) { ring.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val vectorPlusPoint: Plus<Vector3<Number>, Point3<Number>, Point3<Number>> = Plus { left, right ->
        Point3(MDList1.generate(3u) { ring.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val pointMinusVector: Minus<Point3<Number>, Vector3<Number>, Point3<Number>> = Minus { left, right ->
        Point3(MDList1.generate(3u) { ring.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    override val pointMinusPoint: Minus<Point3<Number>, Point3<Number>, Vector3<Number>> = Minus { left, right ->
        Vector3(MDList1.generate(3u) { ring.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    
    override val vectorDotVector: Dot<Vector3<Number>, Vector3<Number>, Number> = Dot { left, right ->
        KoneContext.unwrap(ring)
        (0u ..< 3u).toKoneList().sumOf { left.content[it] * right.content[it] }
    }
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<EuclideanSpace3OverRing<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpace3OverRing<Number>> by lazy {
            ImpliedKeysRegistry {
                EuclideanSpaceOverRing.Key<Number, Vector3<Number>, Point3<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.default3.EuclideanSpace3OverRing.Key<${suppliedTypeOf<Number>()}>"
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> EuclideanSpace3OverRing.Companion.setFor() {
    EuclideanSpace3OverRing.Key<Number>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        EuclideanSpace3OverRing(koneContextRegistry[Ring.Key<Number>()])
    }
}

public class EuclideanSpace3OverField<Number>(
    private val field: Field<Number>,
) : EuclideanSpaceOverField<Number, Vector3<Number>, Point3<Number>> {
    // region Constants
    override val zero: Vector3<Number> = Vector3(MDList1.generate(3u) { field.zero })
    // endregion
    
    // region Equality
    override val numberIsZero: IsZero<Vector3<Number>> = IsZero {
        it.content.all { field.numberIsZero { it.isZero() } }
    }
    // endregion
    
    // region Vector-Int operations
    override val numberTimesInt: Times<Vector3<Number>, Int, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { field.numberTimesInt { it * right } })
    }
    override val vectorDivideInt: Divide<Vector3<Number>, Int, Vector3<Number>> = Divide { left, right ->
        Vector3(left.content.map { field.numberDivideInt { it / right } })
    }
    // endregion
    
    // region Vector-UInt operations
    override val numberTimesUInt: Times<Vector3<Number>, UInt, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { field.numberTimesUInt { it * right } })
    }
    override val vectorDivideUInt: Divide<Vector3<Number>, UInt, Vector3<Number>> = Divide { left, right ->
        Vector3(left.content.map { field.numberDivideUInt { it / right } })
    }
    // endregion
    
    // region Vector-Long operations
    override val numberTimesLong: Times<Vector3<Number>, Long, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { field.numberTimesLong { it * right } })
    }
    override val vectorDivideLong: Divide<Vector3<Number>, Long, Vector3<Number>> = Divide { left, right ->
        Vector3(left.content.map { field.numberDivideLong { it / right } })
    }
    // endregion
    
    // region Vector-ULong operations
    override val numberTimesULong: Times<Vector3<Number>, ULong, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { field.numberTimesULong { it * right } })
    }
    override val vectorDivideULong: Divide<Vector3<Number>, ULong, Vector3<Number>> = Divide { left, right ->
        Vector3(left.content.map { field.numberDivideULong { it / right } })
    }
    // endregion
    
    // region Vector-Number operations
    override val vectorTimesNumber: Times<Vector3<Number>, Number, Vector3<Number>> = Times { left, right ->
        Vector3(left.content.map { field.numberTimesNumber { it * right } })
    }
    override val vectorDivideNumber: Divide<Vector3<Number>, Number, Vector3<Number>> = Divide { left, right ->
        Vector3(left.content.map { field.numberDivideNumber { it / right } })
    }
    // endregion
    
    // region Int-Vector operations
    override val intTimesNumber: Times<Int, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { field.intTimesNumber { left * it } })
    }
    // endregion
    
    // region UInt-Vector operations
    override val uIntTimesNumber: Times<UInt, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { field.uIntTimesNumber { left * it } })
    }
    // endregion
    
    // region Long-Vector operations
    override val longTimesNumber: Times<Long, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { field.longTimesNumber { left * it } })
    }
    // endregion
    
    // region ULong-Vector operations
    override val uLongTimesNumber: Times<ULong, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { field.uLongTimesNumber { left * it } })
    }
    // endregion
    
    // region Number-Vector operations
    override val numberTimesVector: Times<Number, Vector3<Number>, Vector3<Number>> = Times { left, right ->
        Vector3(right.content.map { field.numberTimesNumber { left * it } })
    }
    // endregion
    
    // region Vector-Vector operations
    override val numberUnaryMinus: UnaryMinus<Vector3<Number>, Vector3<Number>> = UnaryMinus {
        Vector3(it.content.map { field.numberUnaryMinus { -it } })
    }
    override val numberPlusNumber: Plus<Vector3<Number>, Vector3<Number>, Vector3<Number>> = Plus { left, right ->
        Vector3(MDList1.generate(3u) { field.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val numberMinusNumber: Minus<Vector3<Number>, Vector3<Number>, Vector3<Number>> = Minus { left, right ->
        Vector3(MDList1.generate(3u) { field.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    // endregion
    
    override val pointPlusVector: Plus<Point3<Number>, Vector3<Number>, Point3<Number>> = Plus { left, right ->
        Point3(MDList1.generate(3u) { field.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val vectorPlusPoint: Plus<Vector3<Number>, Point3<Number>, Point3<Number>> = Plus { left, right ->
        Point3(MDList1.generate(3u) { field.numberPlusNumber { left.content[it] + right.content[it] } })
    }
    override val pointMinusVector: Minus<Point3<Number>, Vector3<Number>, Point3<Number>> = Minus { left, right ->
        Point3(MDList1.generate(3u) { field.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    override val pointMinusPoint: Minus<Point3<Number>, Point3<Number>, Vector3<Number>> = Minus { left, right ->
        Vector3(MDList1.generate(3u) { field.numberMinusNumber { left.content[it] - right.content[it] } })
    }
    
    override val vectorDotVector: Dot<Vector3<Number>, Vector3<Number>, Number> = Dot { left, right ->
        context(field, field.numberTimesNumber) { (0u ..< 3u).toKoneList().sumOf { left.content[it] * right.content[it] } }
    }
    
    public companion object;
    
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<EuclideanSpace3OverField<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpace3OverField<Number>> by lazy {
            ImpliedKeysRegistry {
                EuclideanSpaceOverField.Key<Number, Vector3<Number>, Point3<Number>>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.default3.EuclideanSpace3OverField.Key<${suppliedTypeOf<Number>()}>"
    }
}

@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <@Supply Number> EuclideanSpace3OverField.Companion.setFor() {
    EuclideanSpace3OverField.Key<Number>() correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        EuclideanSpace3OverField(koneContextRegistry[Field.Key<Number>()])
    }
}