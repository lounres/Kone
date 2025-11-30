/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.default2

import dev.lounres.kone.algebraic.Field
import dev.lounres.kone.algebraic.Ring
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.collections.array.KoneDoubleArray
import dev.lounres.kone.collections.array.of
import dev.lounres.kone.collections.interop.toKoneList
import dev.lounres.kone.collections.utils.sumOf
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField
import dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.MDSize
import dev.lounres.kone.multidimensionalCollections.contentEquals
import dev.lounres.kone.multidimensionalCollections.of
import dev.lounres.kone.multidimensionalCollections.utils.all
import dev.lounres.kone.multidimensionalCollections.utils.map
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.relations.eq
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KVariance.OUT


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
    return Vector2(MDList1(contentArray.size) { contentArray[it] })
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
        MDList1(2u) {
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
    override val zero: Vector2<Number> = Vector2(MDList1(2u) { ring.zero })
    // endregion
    
    // region Equality
    override fun Vector2<Number>.equalsTo(other: Vector2<Number>): Boolean =
        (0u ..< 2u).all { ring { this.content[it] eq other.content[it] } }
    override fun Vector2<Number>.isZero(): Boolean =
        this.content.all { ring { it.isZero() } }
    override fun Vector2<Number>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun Vector2<Number>.times(other: UInt): Vector2<Number> =
        Vector2(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-Int operations
    override operator fun Vector2<Number>.times(other: Int): Vector2<Number> =
        Vector2(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-Long operations
    override operator fun Vector2<Number>.times(other: Long): Vector2<Number> =
        Vector2(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-ULong operations
    override operator fun Vector2<Number>.times(other: ULong): Vector2<Number> =
        Vector2(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-Number operations
    override fun Vector2<Number>.times(other: Number): Vector2<Number> =
        Vector2(this.content.map { ring { it * other } })
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { ring { this * it } })
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { ring { this * it } })
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { ring { this * it } })
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { ring { this * it } })
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { ring { this * it } })
    // endregion
    
    // region Vector-Vector operations
    override operator fun Vector2<Number>.unaryMinus(): Vector2<Number> =
        Vector2(this.content.map { ring { -it } })
    override operator fun Vector2<Number>.plus(other: Vector2<Number>): Vector2<Number> =
        Vector2(MDList1(2u) { ring { this.content[it] + other.content[it] } })
    override operator fun Vector2<Number>.minus(other: Vector2<Number>): Vector2<Number> =
        Vector2(MDList1(2u) { ring { this.content[it] - other.content[it] } })
    // endregion
    
    override fun Point2<Number>.plus(other: Vector2<Number>): Point2<Number> =
        Point2(MDList1(2u) { ring { this.content[it] + other.content[it] } })
    
    override fun Vector2<Number>.plus(other: Point2<Number>): Point2<Number> =
        Point2(MDList1(2u) { ring { this.content[it] + other.content[it] } })
    
    override fun Point2<Number>.minus(other: Vector2<Number>): Point2<Number> =
        Point2(MDList1(2u) { ring { this.content[it] - other.content[it] } })
    
    override fun Point2<Number>.minus(other: Point2<Number>): Vector2<Number> =
        Vector2(MDList1(2u) { ring { this.content[it] - other.content[it] } })
    
    override fun Vector2<Number>.dot(other: Vector2<Number>): Number =
        ring { (0u ..< 2u).toKoneList().sumOf { this.content[it] * other.content[it] } }
    
    public companion object;
    
    public class Key<Number>(
        elementType: SuppliedType,
    ) : RegistryKey<EuclideanSpace2OverRing<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverRing",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Vector2",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = elementType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Point2",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = elementType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                ),
                isNullable = false
            )
        @OptIn(DelicateSuppliedTypeConstructor::class)
        override val superkeys: List<RegistryKey<in EuclideanSpace2OverRing<Number>>> =
            listOf(
                EuclideanSpaceOverRing.Key(
                    elementType,
                    SuppliedType.Regular(
                        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Vector2",
                        typeArguments = listOf(
                            SuppliedProjection.Regular(
                                variance = OUT,
                                type = elementType
                            ),
                        ),
                        isNullable = false
                    ),
                    SuppliedType.Regular(
                        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Point2",
                        typeArguments = listOf(
                            SuppliedProjection.Regular(
                                variance = OUT,
                                type = elementType
                            ),
                        ),
                        isNullable = false
                    ),
                ),
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun EuclideanSpace2OverRing.Companion.setFor(numberType: SuppliedType): Unit = with(koneContextRegistryBuilder) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    EuclideanSpace2OverRing.Key<Number>(numberType) correspondsTo EuclideanSpace2OverRing(this[Ring.Key<Number>(numberType)])
}

public class EuclideanSpace2OverField<Number>(
    private val field: Field<Number>,
) : EuclideanSpaceOverField<Number, Vector2<Number>, Point2<Number>> {
    // region Constants
    override val zero: Vector2<Number> = Vector2(MDList1(2u) { field.zero })
    // endregion
    
    // region Equality
    override fun Vector2<Number>.equalsTo(other: Vector2<Number>): Boolean =
        (0u ..< 2u).all { field { this.content[it] eq other.content[it] } }
    override fun Vector2<Number>.isZero(): Boolean =
        this.content.all { field { it.isZero() } }
    override fun Vector2<Number>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun Vector2<Number>.times(other: UInt): Vector2<Number> =
        Vector2(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-Int operations
    override operator fun Vector2<Number>.times(other: Int): Vector2<Number> =
        Vector2(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-Long operations
    override operator fun Vector2<Number>.times(other: Long): Vector2<Number> =
        Vector2(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-ULong operations
    override operator fun Vector2<Number>.times(other: ULong): Vector2<Number> =
        Vector2(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-Number operations
    override fun Vector2<Number>.times(other: Number): Vector2<Number> =
        Vector2(this.content.map { field { it * other } })
    override fun Vector2<Number>.div(other: Number): Vector2<Number> =
        Vector2(this.content.map { field { it / other } })
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { field { this * it } })
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { field { this * it } })
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { field { this * it } })
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { field { this * it } })
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: Vector2<Number>): Vector2<Number> =
        Vector2(other.content.map { field { this * it } })
    // endregion
    
    // region Vector-Vector operations
    override operator fun Vector2<Number>.unaryMinus(): Vector2<Number> =
        Vector2(this.content.map { field { -it } })
    override operator fun Vector2<Number>.plus(other: Vector2<Number>): Vector2<Number> =
        Vector2(MDList1(2u) { field { this.content[it] + other.content[it] } })
    override operator fun Vector2<Number>.minus(other: Vector2<Number>): Vector2<Number> =
        Vector2(MDList1(2u) { field { this.content[it] - other.content[it] } })
    // endregion
    
    override fun Point2<Number>.plus(other: Vector2<Number>): Point2<Number> =
        Point2(MDList1(2u) { field { this.content[it] + other.content[it] } })
    
    override fun Vector2<Number>.plus(other: Point2<Number>): Point2<Number> =
        Point2(MDList1(2u) { field { this.content[it] + other.content[it] } })
    
    override fun Point2<Number>.minus(other: Vector2<Number>): Point2<Number> =
        Point2(MDList1(2u) { field { this.content[it] - other.content[it] } })
    
    override fun Point2<Number>.minus(other: Point2<Number>): Vector2<Number> =
        Vector2(MDList1(2u) { field { this.content[it] - other.content[it] } })
    
    override fun Vector2<Number>.dot(other: Vector2<Number>): Number =
        field { (0u ..< 2u).toKoneList().sumOf { this.content[it] * other.content[it] } }
    
    public companion object;
    
    public class Key<Number>(
        elementType: SuppliedType,
    ) : RegistryKey<EuclideanSpace2OverField<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.EuclideanSpace2OverField",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Vector2",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = elementType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Point2",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = elementType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                ),
                isNullable = false
            )
        @OptIn(DelicateSuppliedTypeConstructor::class)
        override val superkeys: List<RegistryKey<in EuclideanSpace2OverField<Number>>> =
            listOf(
                EuclideanSpaceOverField.Key(
                    elementType,
                    SuppliedType.Regular(
                        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Vector2",
                        typeArguments = listOf(
                            SuppliedProjection.Regular(
                                variance = OUT,
                                type = elementType
                            ),
                        ),
                        isNullable = false
                    ),
                    SuppliedType.Regular(
                        fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Point2",
                        typeArguments = listOf(
                            SuppliedProjection.Regular(
                                variance = OUT,
                                type = elementType
                            ),
                        ),
                        isNullable = false
                    ),
                ),
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun EuclideanSpace2OverField.Companion.setFor(numberType: SuppliedType): Unit = with(koneContextRegistryBuilder) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    EuclideanSpace2OverField.Key<Number>(numberType) correspondsTo EuclideanSpace2OverField(this[Field.Key<Number>(numberType)])
}