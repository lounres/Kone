/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.ExperimentalKoneAPI
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KVariance


/**
 * Wrapper class for a result of Euclidean division (a.k.a. a division with remainder).
 * See [EuclideanSemiring]'s or [EuclideanRing]'s docs for more.
 */
@ExperimentalKoneAPI
@Serializable
//@JvmInline // FIXME: Make it a value class when MFVC will be ready
public /*value*/ data class EuclideanDivisionResult<Number>(public val quotient: Number, public val remainder: Number) {
//    public operator fun component1(): Number = quotient
//    public operator fun component2(): Number = remainder
}

// TODO: Add a link to the interface's reasoning at the docs here at the end.
/**
 * Describes a context that represents Euclidean semiring
 * (i.e. [Euclidean ring (a.k.a. Euclidean domain)](https://en.wikipedia.org/wiki/Euclidean_domain) but without subtraction).
 * It means that it extends [Semiring] interface,
 * and besides ring's operations also provides Euclidean division (a.k.a. division with remainder)
 * that takes dividend and divisor and returns quotient and remainder,
 * where either remainder is zero or has less Euclidean norm.
 *
 * > **Note!**
 * Euclidean norm is not provided by the interface.
 * It means that only the Euclidean division operations are provided
 * mentioning that *there is* some Euclidean norm, so that the operations satisfy conditions on it,
 * but no actual programming representation of the norm is not provided.
 */
@ExperimentalKoneAPI
public interface EuclideanSemiring<Number> : Semiring<Number> {
    /**
     * Returns result of Euclidean division (a.k.a. a division with remainder), both quotient and remainder.
     */
    public infix fun Number.divrem(other: Number): EuclideanDivisionResult<Number>
    /**
     * Returns quotient of Euclidean division (a.k.a. a division with remainder).
     */
    public operator fun Number.div(other: Number): Number = (this divrem other).quotient
    /**
     * Returns remainder of Euclidean division (a.k.a. a division with remainder).
     */
    public operator fun Number.rem(other: Number): Number = (this divrem other).remainder
    
    /**
     * Registry key for [EuclideanSemiring] interface in [KoneContextRegistry].
     */
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<EuclideanSemiring<Number>> {
        override val typeKey: SuppliedType.Regular<EuclideanSemiring<Number>> =
            SuppliedType.Regular(
                kClass = EuclideanSemiring::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

/**
 * Returns result of Euclidean division (a.k.a. a division with remainder), both quotient and remainder.
 *
 * A bridge contextual function for [EuclideanSemiring.divrem].
 */
context(ring: EuclideanSemiring<Number>)
@ExperimentalKoneAPI
public infix fun <Number> Number.divrem(other: Number): EuclideanDivisionResult<Number> = with(ring) { this@divrem divrem other }
/**
 * Returns quotient of Euclidean division (a.k.a. a division with remainder).
 *
 * A bridge contextual function for [EuclideanSemiring.div].
 */
context(ring: EuclideanSemiring<Number>)
@ExperimentalKoneAPI
public operator fun <Number> Number.div(other: Number): Number = with(ring) { this@div / other }
/**
 * Returns remainder of Euclidean division (a.k.a. a division with remainder).
 *
 * A bridge contextual function for [EuclideanSemiring.rem].
 */
context(ring: EuclideanSemiring<Number>)
@ExperimentalKoneAPI
public operator fun <Number> Number.rem(other: Number): Number = with(ring) { this@rem % other }

// TODO: Add a link to the interface's reasoning at the docs here at the end.
/**
 * Describes a context that represents [Euclidean ring (a.k.a. Euclidean domain)](https://en.wikipedia.org/wiki/Euclidean_domain).
 * It means that it extends both [Ring] and [EuclideanSemiring] interfaces without adding anything new to them.
 * Just a composition of this two.
 */
@ExperimentalKoneAPI
public interface EuclideanRing<Number> : Ring<Number>, EuclideanSemiring<Number> {
    /**
     * Registry key for [EuclideanRing] interface in [KoneContextRegistry].
     */
    public class Key<Number>(
        elementType: SuppliedType<Number>,
    ) : RegistryKey<EuclideanRing<Number>> {
        override val typeKey: SuppliedType.Regular<EuclideanRing<Number>> =
            SuppliedType.Regular(
                kClass = EuclideanRing::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}