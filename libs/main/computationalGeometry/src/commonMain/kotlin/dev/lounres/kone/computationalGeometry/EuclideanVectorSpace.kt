/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


public interface EuclideanVectorSpaceOverRing<Number, Vector> : Module<Number, Vector> {
    public infix fun Vector.dot(other: Vector): Number
    
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<EuclideanVectorSpaceOverRing<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverRing",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                ),
                isNullable = false
            )
        override val superkeys: List<RegistryKey<in EuclideanVectorSpaceOverRing<Number, Vector>>> =
            listOf(
                Module.Key(elementType, vectorType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
public infix fun <Number, Vector> Vector.dot(other: Vector): Number = with(euclideanSpace) { this@dot dot other }

context(euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
public fun <Number, Vector> Vector.lengthSquared(): Number = with(euclideanSpace) { this@lengthSquared dot this@lengthSquared }

public interface EuclideanVectorSpaceOverField<Number, Vector> : VectorSpace<Number, Vector>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<EuclideanVectorSpaceOverField<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverField",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = elementType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                ),
                isNullable = false
            )
        override val superkeys: List<RegistryKey<in EuclideanVectorSpaceOverField<Number, Vector>>> =
            listOf(
                VectorSpace.Key(elementType, vectorType),
                EuclideanVectorSpaceOverRing.Key(elementType, vectorType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}