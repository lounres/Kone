/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry

import dev.lounres.kone.algebraic.Module
import dev.lounres.kone.algebraic.VectorSpace
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


public interface EuclideanVectorSpaceOverRing<Number, Vector> : Module<Number, Vector> {
    public infix fun Vector.dot(other: Vector): Number
    
    public companion object;
    
    public class Key<Number, Vector>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
    ) : RegistryKey<EuclideanVectorSpaceOverRing<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverRing",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<EuclideanVectorSpaceOverRing<Number, Vector>> = ImpliedKeysRegistry {
            Module.Key<Number, Vector>(numberType, vectorType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverRing.Key<$numberType, $vectorType>"
    }
}

context(euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
public infix fun <Number, Vector> Vector.dot(other: Vector): Number = with(euclideanSpace) { this@dot dot other }

context(euclideanSpace: EuclideanVectorSpaceOverRing<Number, Vector>)
public fun <Number, Vector> Vector.lengthSquared(): Number = with(euclideanSpace) { this@lengthSquared dot this@lengthSquared }

public interface EuclideanVectorSpaceOverField<Number, Vector> : VectorSpace<Number, Vector>, EuclideanVectorSpaceOverRing<Number, Vector> {
    public companion object;
    
    public class Key<Number, Vector>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
    ) : RegistryKey<EuclideanVectorSpaceOverField<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverField",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = numberType
                    ),
                    SuppliedProjection.Regular(
                        variance = INVARIANT,
                        type = vectorType
                    ),
                ),
                isNullable = false
            )
        override val impliedKeys: ImpliedKeysRegistry<EuclideanVectorSpaceOverField<Number, Vector>> = ImpliedKeysRegistry {
            VectorSpace.Key<Number, Vector>(numberType, vectorType) implies { it }
            EuclideanVectorSpaceOverRing.Key<Number, Vector>(numberType, vectorType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.computationalGeometry.EuclideanVectorSpaceOverField.Key<$numberType, $vectorType>"
    }
}