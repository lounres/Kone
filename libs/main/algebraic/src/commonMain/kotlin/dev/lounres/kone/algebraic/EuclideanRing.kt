/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.ExperimentalKoneAPI
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
import kotlin.reflect.KVariance


/**
 * Wrapper class for result of Euclidean division (a.k.a. division with remainder). See [EuclideanRing] for more.
 */
@ExperimentalKoneAPI
@Serializable
//@JvmInline // FIXME: Make it a value class when MFVC will be ready
public /*value*/ data class EuclideanDivisionResult<Number>(public val quotient: Number, public val remainder: Number) {
//    public operator fun component1(): Number = quotient
//    public operator fun component2(): Number = remainder
}

/**
 * Describes a context that represents [Euclidean ring](https://en.wikipedia.org/wiki/Euclidean_domain).
 * It means that it extends [Ring] interface and besides ring's operations also provides Euclidean division
 * (a.k.a. division with remainder) that takes dividend and divisor and returns quotient and remainder,
 * where either remainder is zero or has less Euclidean norm.
 * See definition of [Euclidean ring on Wikipedia](https://en.wikipedia.org/wiki/Euclidean_domain)
 * for a more thorough description of the Euclidean division and the Euclidean norm.
 */
@ExperimentalKoneAPI
public interface EuclideanSemiring<Number> : Semiring<Number> {
    /**
     * Returns result of Euclidean division (a.k.a. division with remainder), both quotient and remainder.
     */
    public infix fun Number.divrem(other: Number): EuclideanDivisionResult<Number>
    /**
     * Returns quotient of Euclidean division (a.k.a. division with remainder).
     */
    public operator fun Number.div(other: Number): Number = (this divrem other).quotient
    /**
     * Returns remainder of Euclidean division (a.k.a. division with remainder).
     */
    public operator fun Number.rem(other: Number): Number = (this divrem other).remainder
    
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

context(ring: EuclideanSemiring<Number>)
@ExperimentalKoneAPI
public infix fun <Number> Number.divrem(other: Number): EuclideanDivisionResult<Number> = with(ring) { this@divrem divrem other }
context(ring: EuclideanSemiring<Number>)
@ExperimentalKoneAPI
public operator fun <Number> Number.div(other: Number): Number = with(ring) { this@div / other }
context(ring: EuclideanSemiring<Number>)
@ExperimentalKoneAPI
public operator fun <Number> Number.rem(other: Number): Number = with(ring) { this@rem % other }

@ExperimentalKoneAPI
public interface EuclideanRing<Number> : Ring<Number>, EuclideanSemiring<Number> {
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