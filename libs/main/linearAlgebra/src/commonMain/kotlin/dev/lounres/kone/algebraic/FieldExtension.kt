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


@Suppress("INAPPLICABLE_JVM_NAME")
public interface FieldExtension<Number, Vector> : CommutativeAlgebra<Number, Vector>, Field<Vector> {
    // region Vector-Number operations
    @JvmName("divVectorNumber")
    public operator fun Vector.div(other: Number): Vector = this / valueOf(other)
    // endregion
    
    // region Number-Vector operations
    @JvmName("divNumberVector")
    public operator fun Number.div(other: Vector): Vector = valueOf(this) / other
    // endregion
    
    public companion object;
    
    public class Key<Number, Vector>(
        public val numberType: SuppliedType,
        public val vectorType: SuppliedType,
    ) : RegistryKey<FieldExtension<Number, Vector>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.algebraic.FieldExtension",
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
        override val impliedKeys: ImpliedKeysRegistry<FieldExtension<Number, Vector>> = ImpliedKeysRegistry {
            Algebra.Key<Number, Vector>(numberType, vectorType).impliesSame()
            Field.Key<Vector>(vectorType).impliesSame()
        }
        override fun equals(other: Any?): Boolean = other is Key<*, *> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.algebraic.FieldExtension.Key<$numberType, $vectorType>"
    }
}

@JvmName("divVectorNumber")
context(fieldExtension: FieldExtension<Number, Vector>)
public operator fun <Number, Vector> Vector.div(other: Number): Vector = with(fieldExtension) { this@div / other }

@JvmName("divNumberVector")
context(fieldExtension: FieldExtension<Number, Vector>)
public operator fun <Number, Vector> Number.div(other: Vector): Vector = with(fieldExtension) { this@div / other }