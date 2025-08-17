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


// unital, associative
public interface Algebra<Number, Vector> : Module<Number, Vector>, Ring<Vector> {
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<Algebra<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Algebra",
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
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

public interface CommutativeAlgebra<Number, Vector> : Algebra<Number, Vector>, CommutativeRing<Vector> {
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<CommutativeAlgebra<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.CommutativeAlgebra",
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
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}