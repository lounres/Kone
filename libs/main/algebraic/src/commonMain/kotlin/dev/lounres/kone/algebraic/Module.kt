/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.jvm.JvmName
import kotlin.reflect.KVariance.INVARIANT


@Suppress("INAPPLICABLE_JVM_NAME")
public interface LeftModule<Number, Vector> : CommutativeGroup<Vector> {
    @JvmName("timesNumberVector")
    public operator fun Number.times(other: Vector): Vector
    
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<LeftModule<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.LeftModule",
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
        override val superkeys: List<RegistryKey<in LeftModule<Number, Vector>>> =
            listOf(
                CommutativeGroup.Key(vectorType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(module: LeftModule<Number, Vector>)
public operator fun <Number, Vector> Number.times(other: Vector): Vector = with(module) { this@times * other }

@Suppress("INAPPLICABLE_JVM_NAME")
public interface RightModule<Number, Vector> : CommutativeGroup<Vector> {
    @JvmName("timesVectorNumber")
    public operator fun Vector.times(other: Number): Vector
    
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<RightModule<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.RightModule",
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
        override val superkeys: List<RegistryKey<in RightModule<Number, Vector>>> =
            listOf(
                CommutativeGroup.Key(vectorType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

context(module: RightModule<Number, Vector>)
public operator fun <Number, Vector> Vector.times(other: Number): Vector = with(module) { this@times * other }

// The underlying ring is commutative
public interface Module<Number, Vector> : LeftModule<Number, Vector>, RightModule<Number, Vector> {
    public companion object;
    
    public class Key<Number, Vector>(
        elementType: SuppliedType,
        vectorType: SuppliedType,
    ) : RegistryKey<Module<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Module",
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
        override val superkeys: List<RegistryKey<in Module<Number, Vector>>> =
            listOf(
                LeftModule.Key(elementType, vectorType),
                RightModule.Key(elementType, vectorType),
            )
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}