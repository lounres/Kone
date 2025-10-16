/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


// The underlying ring is ordered (thus, is a subring of real numbers and is an integral domain)
public interface EuclideanSpaceOverRing<Number, Vector, Point> : AffineSpaceOverRing<Number, Vector, Point>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
    
    public class Key<Number, Vector, Point>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
        pointType: SuppliedType,
    ) : RegistryKey<EuclideanSpaceOverRing<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanSpaceOverRing",
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

context(_: EuclideanSpaceOverRing<Number, Vector, Point>)
public fun <Number, Vector, Point> distanceSquaredBetween(point1: Point, point2: Point): Number =
    (point1 - point2).lengthSquared()

public interface EuclideanSpaceOverField<Number, Vector, Point> : EuclideanSpaceOverRing<Number, Vector, Point>, AffineSpaceOverField<Number, Vector, Point>, EuclideanVectorSpaceOverField<Number, Vector> {
    public companion object;
    
    public class Key<Number, Vector, Point>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
        pointType: SuppliedType,
    ) : RegistryKey<EuclideanSpaceOverField<Number, Vector, Point>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanSpaceOverField",
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

public fun <Number, Vector, Point, Result> KoneContextRegistry.inEuclideanSpaceOverFieldScopeFor(
    numberType: SuppliedType,
    vectorType: SuppliedType,
    pointType: SuppliedType,
    block: context(EuclideanSpaceOverField<Number, Vector, Point>) () -> Result
): Result = block(this[EuclideanSpaceOverField.Key<Number, Vector, Point>(numberType, vectorType, pointType)])