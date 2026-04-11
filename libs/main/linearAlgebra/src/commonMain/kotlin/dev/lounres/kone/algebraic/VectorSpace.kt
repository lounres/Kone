/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType


public interface VectorSpace<Number, Vector> : Module<Number, Vector> {
    public operator fun Vector.div(other: Number): Vector
    
    public companion object;
    
    public class Key<Number, Vector>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
    ) : RegistryKey<VectorSpace<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.VectorSpace",
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
        override val impliedKeys: ImpliedKeysRegistry<VectorSpace<Number, Vector>> = ImpliedKeysRegistry {
            Module.Key<Number, Vector>(numberType, vectorType).impliesSame()
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.VectorSpace.Key<$numberType, $vectorType>"
    }
    
    public interface FiniteDimensional<Number, Vector> : VectorSpace<Number, Vector> {
        public val dimension: UInt
        
        public companion object;
        
        public class Key<Number, Vector>(
            public val numberType: SuppliedType,
            public val vectorType: SuppliedType,
        ) : RegistryKey<FiniteDimensional<Number, Vector>> {
            public val typeKey: SuppliedType.Regular =
                @OptIn(DelicateSuppliedTypeConstructor::class)
                SuppliedType.Regular(
                    fullyQualifiedName = "dev.lounres.kone.algebraic.VectorSpace.FiniteDimensional",
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
            override val impliedKeys: ImpliedKeysRegistry<FiniteDimensional<Number, Vector>> = ImpliedKeysRegistry {
                VectorSpace.Key<Number, Vector>(numberType, vectorType).impliesSame()
            }
            override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
            override fun hashCode(): Int = typeKey.hashCode()
            override fun toString(): String = "dev.lounres.kone.algebraic.VectorSpace.FiniteDimensional.Key<$numberType, $vectorType>"
        }
    }
}

context(vectorSpace: VectorSpace<Number, Vector>)
public operator fun <Number, Vector> Vector.div(other: Number): Vector = with(vectorSpace) { this@div / other }