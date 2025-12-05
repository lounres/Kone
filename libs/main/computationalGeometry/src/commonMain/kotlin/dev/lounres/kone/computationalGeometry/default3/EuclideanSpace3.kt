/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.default3

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
import dev.lounres.kone.registry.ImpliedKeysRegistry
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
    return Vector3(MDList1(contentArray.size) { contentArray[it] })
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
        MDList1(3u) {
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
    override val zero: Vector3<Number> = Vector3(MDList1(3u) { ring.zero })
    // endregion
    
    // region Equality
    override fun Vector3<Number>.equalsTo(other: Vector3<Number>): Boolean =
        (0u ..< 3u).all { ring { this.content[it] eq other.content[it] } }
    override fun Vector3<Number>.isZero(): Boolean =
        this.content.all { ring { it.isZero() } }
    override fun Vector3<Number>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun Vector3<Number>.times(other: UInt): Vector3<Number> =
        Vector3(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-Int operations
    override operator fun Vector3<Number>.times(other: Int): Vector3<Number> =
        Vector3(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-Long operations
    override operator fun Vector3<Number>.times(other: Long): Vector3<Number> =
        Vector3(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-ULong operations
    override operator fun Vector3<Number>.times(other: ULong): Vector3<Number> =
        Vector3(this.content.map { ring { it * other } })
    // endregion
    
    // region Vector-Number operations
    override fun Vector3<Number>.times(other: Number): Vector3<Number> =
        Vector3(this.content.map { ring { it * other } })
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { ring { this * it } })
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { ring { this * it } })
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { ring { this * it } })
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { ring { this * it } })
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { ring { this * it } })
    // endregion
    
    // region Vector-Vector operations
    override operator fun Vector3<Number>.unaryMinus(): Vector3<Number> =
        Vector3(this.content.map { ring { -it } })
    override operator fun Vector3<Number>.plus(other: Vector3<Number>): Vector3<Number> =
        Vector3(MDList1(3u) { ring { this.content[it] + other.content[it] } })
    override operator fun Vector3<Number>.minus(other: Vector3<Number>): Vector3<Number> =
        Vector3(MDList1(3u) { ring { this.content[it] - other.content[it] } })
    // endregion
    
    override fun Point3<Number>.plus(other: Vector3<Number>): Point3<Number> =
        Point3(MDList1(3u) { ring { this.content[it] + other.content[it] } })
    
    override fun Vector3<Number>.plus(other: Point3<Number>): Point3<Number> =
        Point3(MDList1(3u) { ring { this.content[it] + other.content[it] } })
    
    override fun Point3<Number>.minus(other: Vector3<Number>): Point3<Number> =
        Point3(MDList1(3u) { ring { this.content[it] - other.content[it] } })
    
    override fun Point3<Number>.minus(other: Point3<Number>): Vector3<Number> =
        Vector3(MDList1(3u) { ring { this.content[it] - other.content[it] } })
    
    override fun Vector3<Number>.dot(other: Vector3<Number>): Number =
        ring { (0u ..< 3u).toKoneList().sumOf { this.content[it] * other.content[it] } }
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<EuclideanSpace3OverRing<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.EuclideanSpace3OverRing",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.Vector3",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = numberType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.Point3",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = numberType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                ),
                isNullable = false
            )
        @OptIn(DelicateSuppliedTypeConstructor::class)
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpace3OverRing<Number>> = ImpliedKeysRegistry {
            EuclideanSpaceOverRing.Key<Number, Vector3<Number>, Point3<Number>>(
                numberType,
                SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.Vector3",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType
                        ),
                    ),
                    isNullable = false
                ),
                SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Point3",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType
                        ),
                    ),
                    isNullable = false
                ),
            ) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.default3.EuclideanSpace3OverRing.Key<$numberType>"
    }
}

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun EuclideanSpace3OverRing.Companion.setFor(numberType: SuppliedType) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    EuclideanSpace3OverRing.Key<Number>(numberType) correspondsTo EuclideanSpace3OverRing(koneContextRegistryBuilder[Ring.Key<Number>(numberType)])
}

public class EuclideanSpace3OverField<Number>(
    private val field: Field<Number>,
) : EuclideanSpaceOverField<Number, Vector3<Number>, Point3<Number>> {
    // region Constants
    override val zero: Vector3<Number> = Vector3(MDList1(3u) { field.zero })
    // endregion
    
    // region Equality
    override fun Vector3<Number>.equalsTo(other: Vector3<Number>): Boolean =
        (0u ..< 3u).all { field { this.content[it] eq other.content[it] } }
    override fun Vector3<Number>.isZero(): Boolean =
        this.content.all { field { it.isZero() } }
    override fun Vector3<Number>.isNotZero(): Boolean = !isZero()
    // endregion
    
    // region Vector-UInt operations
    override operator fun Vector3<Number>.times(other: UInt): Vector3<Number> =
        Vector3(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-Int operations
    override operator fun Vector3<Number>.times(other: Int): Vector3<Number> =
        Vector3(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-Long operations
    override operator fun Vector3<Number>.times(other: Long): Vector3<Number> =
        Vector3(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-ULong operations
    override operator fun Vector3<Number>.times(other: ULong): Vector3<Number> =
        Vector3(this.content.map { field { it * other } })
    // endregion
    
    // region Vector-Number operations
    override fun Vector3<Number>.times(other: Number): Vector3<Number> =
        Vector3(this.content.map { field { it * other } })
    override fun Vector3<Number>.div(other: Number): Vector3<Number> =
        Vector3(this.content.map { field { it / other } })
    // endregion
    
    // region Int-Vector operations
    override operator fun Int.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { field { this * it } })
    // endregion
    
    // region UInt-Vector operations
    override operator fun UInt.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { field { this * it } })
    // endregion
    
    // region Long-Vector operations
    override operator fun Long.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { field { this * it } })
    // endregion
    
    // region ULong-Vector operations
    override operator fun ULong.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { field { this * it } })
    // endregion
    
    // region Number-Vector operations
    override fun Number.times(other: Vector3<Number>): Vector3<Number> =
        Vector3(other.content.map { field { this * it } })
    // endregion
    
    // region Vector-Vector operations
    override operator fun Vector3<Number>.unaryMinus(): Vector3<Number> =
        Vector3(this.content.map { field { -it } })
    override operator fun Vector3<Number>.plus(other: Vector3<Number>): Vector3<Number> =
        Vector3(MDList1(3u) { field { this.content[it] + other.content[it] } })
    override operator fun Vector3<Number>.minus(other: Vector3<Number>): Vector3<Number> =
        Vector3(MDList1(3u) { field { this.content[it] - other.content[it] } })
    // endregion
    
    override fun Point3<Number>.plus(other: Vector3<Number>): Point3<Number> =
        Point3(MDList1(3u) { field { this.content[it] + other.content[it] } })
    
    override fun Vector3<Number>.plus(other: Point3<Number>): Point3<Number> =
        Point3(MDList1(3u) { field { this.content[it] + other.content[it] } })
    
    override fun Point3<Number>.minus(other: Vector3<Number>): Point3<Number> =
        Point3(MDList1(3u) { field { this.content[it] - other.content[it] } })
    
    override fun Point3<Number>.minus(other: Point3<Number>): Vector3<Number> =
        Vector3(MDList1(3u) { field { this.content[it] - other.content[it] } })
    
    override fun Vector3<Number>.dot(other: Vector3<Number>): Number =
        field { (0u ..< 3u).toKoneList().sumOf { this.content[it] * other.content[it] } }
    
    public companion object;
    
    public class Key<Number>(
        public val numberType: SuppliedType,
    ) : RegistryKey<EuclideanSpace3OverField<Number>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.EuclideanSpace3OverField",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.Vector3",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = numberType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = SuppliedType.Regular(
                            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.Point3",
                            typeArguments = listOf(
                                SuppliedProjection.Regular(
                                    variance = OUT,
                                    type = numberType
                                ),
                            ),
                            isNullable = false
                        ),
                    ),
                ),
                isNullable = false
            )
        @OptIn(DelicateSuppliedTypeConstructor::class)
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSpace3OverField<Number>> = ImpliedKeysRegistry {
            EuclideanSpaceOverField.Key<Number, Vector3<Number>, Point3<Number>>(
                numberType,
                SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default3.Vector3",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType
                        ),
                    ),
                    isNullable = false
                ),
                SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.computationalGeometry.default2.Point3",
                    typeArguments = listOf(
                        SuppliedProjection.Regular(
                            variance = OUT,
                            type = numberType
                        ),
                    ),
                    isNullable = false
                ),
            ) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.default3.EuclideanSpace3OverField.Key<$numberType>"
    }
}

context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun EuclideanSpace3OverField.Companion.setFor(numberType: SuppliedType) {
    @OptIn(DelicateSuppliedTypeConstructor::class)
    EuclideanSpace3OverField.Key<Number>(numberType) correspondsTo EuclideanSpace3OverField(koneContextRegistryBuilder[Field.Key<Number>(numberType)])
}