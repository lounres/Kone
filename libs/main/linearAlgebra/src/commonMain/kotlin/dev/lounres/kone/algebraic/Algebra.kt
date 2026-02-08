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
import kotlin.jvm.JvmName
import kotlin.reflect.KVariance.INVARIANT


// unital, associative
@Suppress("INAPPLICABLE_JVM_NAME")
public interface Algebra<Number, Vector> : Module<Number, Vector>, Ring<Vector> {
    // region Number conversion
    public fun valueOf(arg: Number): Vector
    // endregion
    
    // region Vector-Number operations
    @JvmName("plusVectorNumber")
    public operator fun Vector.plus(other: Number): Vector = this + valueOf(other)
    @JvmName("minusVectorNumber")
    public operator fun Vector.minus(other: Number): Vector = this - valueOf(other)
    @JvmName("timesVectorNumber")
    public override fun Vector.times(other: Number): Vector = this * valueOf(other)
    // endregion
    
    // region Number-Vector operations
    @JvmName("plusNumberVector")
    public operator fun Number.plus(other: Vector): Vector = valueOf(this) + other
    @JvmName("minusNumberVector")
    public operator fun Number.minus(other: Vector): Vector = valueOf(this) - other
    @JvmName("timesNumberVector")
    public override fun Number.times(other: Vector): Vector = valueOf(this) * other
    // endregion
    
    public companion object;
    
    public class Key<Number, Vector>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
    ) : RegistryKey<Algebra<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.Algebra",
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
        override val impliedKeys: ImpliedKeysRegistry<Algebra<Number, Vector>> = ImpliedKeysRegistry {
            Module.Key<Number, Vector>(numberType, vectorType) implies { it }
            Ring.Key<Vector>(vectorType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.Algebra.Key<$numberType, $vectorType>"
    }
}

context(algebra: Algebra<Number, Vector>)
public operator fun <Number, Vector> Vector.plus(other: Number): Vector = with(algebra) { this@plus + other }

context(algebra: Algebra<Number, Vector>)
public operator fun <Number, Vector> Vector.minus(other: Number): Vector = with(algebra) { this@minus - other }

//context(algebra: Algebra<Number, Vector>)
//public operator fun <Number, Vector> Number.plus(other: Vector): Vector = with(algebra) { this@plus + other }
//
//context(algebra: Algebra<Number, Vector>)
//public operator fun <Number, Vector> Number.minus(other: Vector): Vector = with(algebra) { this@minus - other }

public interface CommutativeAlgebra<Number, Vector> : Algebra<Number, Vector>, CommutativeRing<Vector> {
    public companion object;
    
    public class Key<Number, Vector>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
    ) : RegistryKey<CommutativeAlgebra<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.CommutativeAlgebra",
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
        override val impliedKeys: ImpliedKeysRegistry<CommutativeAlgebra<Number, Vector>> = ImpliedKeysRegistry {
            Algebra.Key<Number, Vector>(numberType, vectorType) implies { it }
            CommutativeRing.Key<Vector>(vectorType) implies { it }
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeAlgebra.Key<$numberType, $vectorType>"
    }
}