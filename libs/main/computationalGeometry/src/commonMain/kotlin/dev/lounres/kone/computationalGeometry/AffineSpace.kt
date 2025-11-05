/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.algebraic.div
import dev.lounres.kone.algebraic.isNotZero
import dev.lounres.kone.algebraic.isZero
import dev.lounres.kone.algebraic.minus
import dev.lounres.kone.algebraic.plus
import dev.lounres.kone.algebraic.times
import dev.lounres.kone.algebraic.unaryMinus
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.relations.equalsTo
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.jvm.JvmName
import kotlin.reflect.KVariance.INVARIANT


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
        elementType: SuppliedType,
        vectorType: SuppliedType,
        pointType: SuppliedType,
    ) : RegistryKey<AffineSpaceOverRing<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.AffineSpaceOverRing",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
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
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

@JvmName("pointPlusVector")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.plus(other: Vector): Point = with(affineSpace) { this@plus + other }

@JvmName("pointMinusVector")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.minus(other: Vector): Point = with(affineSpace) { this@minus - other }

@JvmName("vectorPlusPoint")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Vector.plus(other: Point): Point = with(affineSpace) { this@plus + other }

@JvmName("vectorMinusPoint")
context(affineSpace: AffineSpaceOverRing<*, Vector, Point>)
public operator fun <Vector, Point> Point.minus(other: Point): Vector = with(affineSpace) { this@minus - other }

public interface AffineSpaceOverField<Number, Vector, Point> : VectorSpace<Number, Vector>, AffineSpaceOverRing<Number, Vector, Point> {
    public companion object;
    
    public class Key<Number, Vector, Point>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
        pointType: SuppliedType,
    ) : RegistryKey<AffineSpaceOverField<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.AffineSpaceOverField",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
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
        override fun equals(other: Any?): Boolean = other is Key<*, *, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
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
context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Number, Vector> AffineSpaceOverField.Companion.setViaVectorSpaceFor(numberType: SuppliedType, vectorType: SuppliedType): Unit = with(koneContextRegistryBuilder) {
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
    ) correspondsTo AffineSpaceOverField.viaVectorSpace(this[VectorSpace.Key<Number, Vector>(numberType, vectorType)])
}