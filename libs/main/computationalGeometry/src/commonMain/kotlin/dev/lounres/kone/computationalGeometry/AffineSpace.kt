/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.jvm.JvmName


@Suppress("INAPPLICABLE_JVM_NAME")
public interface AffineSpaceOverRing<Number, Vector, Point> : Module<Number, Vector> {
    @JvmName("plusPointVector")
    public operator fun Point.plus(other: Vector): Point
    @JvmName("minusPointVector")
    public operator fun Point.minus(other: Vector): Point
    @JvmName("plusVectorPoint")
    public operator fun Vector.plus(other: Point): Point
    @JvmName("minusPointPoint")
    public operator fun Point.minus(other: Point): Vector
    
    public companion object;
    
    public class Key<Number, Vector, Point>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
        public val pointType: SuppliedType,
    ) : RegistryKey<AffineSpaceOverRing<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.AffineSpaceOverRing",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = pointType
                    ),
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<AffineSpaceOverRing<Number, Vector, Point>> = ImpliedKeysRegistry {
            Module.Key<Number, Vector>(numberType, vectorType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.AffineSpaceOverRing.Key<$numberType, $vectorType, $pointType>"
    }
}

@JvmName("pointPlusVector")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.plus(other: Vector): Point = with(affineSpace) { this@plus + other }

//@JvmName("pointMinusVector")
//context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
//public operator fun <Vector, Point> Point.minus(other: Vector): Point = with(affineSpace) { this@minus - other }

//@JvmName("vectorPlusPoint")
//context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
//public operator fun <Vector, Point> Vector.plus(other: Point): Point = with(affineSpace) { this@plus + other }

@JvmName("vectorMinusPoint")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.minus(other: Point): Vector = with(affineSpace) { this@minus - other }

public interface AffineSpaceOverField<Number, Vector, Point> : VectorSpace<Number, Vector>, AffineSpaceOverRing<Number, Vector, Point> {
    public companion object;
    
    public class Key<Number, Vector, Point>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
        public val pointType: SuppliedType,
    ) : RegistryKey<AffineSpaceOverField<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.AffineSpaceOverField",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = pointType
                    ),
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<AffineSpaceOverField<Number, Vector, Point>> = ImpliedKeysRegistry {
            VectorSpace.Key<Number, Vector>(numberType, vectorType) implies { it }
            AffineSpaceOverRing.Key<Number, Vector, Point>(numberType, vectorType, pointType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.AffineSpaceOverField.Key<$numberType, $vectorType, $pointType>"
    }
}

private class AffineSpaceOverFieldViaVectorSpace<Number, Vector>(
    private val vectorSpace: VectorSpace<Number, Vector>,
) : AffineSpaceOverField<Number, Vector, PointWrapper<Vector>>, VectorSpace<Number, Vector> by vectorSpace {
    
    override fun PointWrapper<Vector>.plus(other: Vector): PointWrapper<Vector> =
        vectorSpace { PointWrapper(this.vector + other) }
    
    override fun PointWrapper<Vector>.minus(other: Vector): PointWrapper<Vector> =
        vectorSpace { PointWrapper(this.vector - other) }
    
    override fun Vector.plus(other: PointWrapper<Vector>): PointWrapper<Vector> =
        vectorSpace { PointWrapper(this + other.vector) }
    
    override fun PointWrapper<Vector>.minus(other: PointWrapper<Vector>): Vector =
        vectorSpace { this.vector - other.vector }
}

public fun <Number, Vector> AffineSpaceOverField.Companion.viaVectorSpace(vectorSpace: VectorSpace<Number, Vector>): AffineSpaceOverField<Number, Vector, PointWrapper<Vector>> =
    AffineSpaceOverFieldViaVectorSpace(vectorSpace)

@OptIn(DelicateSuppliedTypeConstructor::class)
context(_: MutableOwnedRegistry<KoneContextRegistry>, koneContextRegistry: KoneContextRegistry.Provider)
public fun <Number, Vector> AffineSpaceOverField.Companion.setViaVectorSpaceFor(numberType: SuppliedType, vectorType: SuppliedType) {
    AffineSpaceOverField.Key<Number, Vector, PointWrapper<Vector>>(
        numberType,
        vectorType,
        SuppliedType.Regular(
            fullyQualifiedName = "dev.lounres.kone.computationalGeometry.PointWrapper",
            typeArguments = listOf(
                SuppliedProjection.Regular(
                    variance = OUT,
                    type = vectorType,
                )
            ),
            isNullable = false,
        ),
    ) correspondsTo RegisteredValueProvider.cached {
        val koneContextRegistry = koneContextRegistry.get()
        viaVectorSpace(koneContextRegistry[VectorSpace.Key<Number, Vector>(numberType, vectorType)])
    }
}