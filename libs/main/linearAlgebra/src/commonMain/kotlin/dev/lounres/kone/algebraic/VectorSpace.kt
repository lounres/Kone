/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance.INVARIANT


public interface VectorSpace<Number, Vector> : Module<Number, Vector> {
    public operator fun Vector.div(other: Number): Vector
    
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<VectorSpace<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebra.VectorSpace",
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
        override val superkeys: List<RegistryKey<in VectorSpace<Number, Vector>>> =
            listOf(
                Module.Key(elementType, vectorType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
    
    public interface FiniteDimensional<Number, Vector> : VectorSpace<Number, Vector> {
        public val dimension: UInt
        
        public companion object;
        
        public class Key<Number, Vector>(
            elementType: SuppliedType,
            vectorType: SuppliedType,
        ) : RegistryKey<FiniteDimensional<Number, Vector>> {
            public val typeKey: SuppliedType.Regular =
                @OptIn(DelicateSuppliedTypeConstructor::class)
                SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.algebra.VectorSpace.FiniteDimensional",
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
            override val superkeys: List<RegistryKey<in FiniteDimensional<Number, Vector>>> =
                listOf(
                    VectorSpace.Key(elementType, vectorType),
                )
            override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
            override fun hashCode(): Int = typeKey.hashCode()
        }
    }
}

context(vectorSpace: VectorSpace<Number, Vector>)
public operator fun <Number, Vector> Vector.div(other: Number): Vector = with(vectorSpace) { this@div / other }