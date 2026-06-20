/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlinx.serialization.Serializable


/**
 * Wrapper class for a result of Euclidean division (a.k.a. a division with remainder).
 * See [EuclideanSemiring]'s or [EuclideanRing]'s docs for more.
 */
@Serializable
//@JvmInline // There might be a problem with the MFVC and context parameters. See KT-72538 for more.
public /*value*/ data class EuclideanDivisionResult<Number>(public val quotient: Number, public val remainder: Number) {
//    public operator fun component1(): Number = quotient
//    public operator fun component2(): Number = remainder
}

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
public interface EuclideanSemiring<Number> : CommutativeSemiring<Number> {
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
    
    public companion object;
    
    /**
     * Registry key for [EuclideanSemiring] interface in [KoneContextRegistry].
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<EuclideanSemiring<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanSemiring<Number>> by lazy {
            ImpliedKeysRegistry {
                CommutativeSemiring.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.EuclideanSemiring.Key<${suppliedTypeOf<Number>()}>"
    }
}

/**
 * Returns result of Euclidean division (a.k.a. a division with remainder), both quotient and remainder.
 *
 * A bridge contextual function for [EuclideanSemiring.divrem].
 */
context(ring: EuclideanSemiring<Number>)
public infix fun <Number> Number.divrem(other: Number): EuclideanDivisionResult<Number> = with(ring) { this@divrem divrem other }
/**
 * Returns quotient of Euclidean division (a.k.a. a division with remainder).
 *
 * A bridge contextual function for [EuclideanSemiring.div].
 */
context(ring: EuclideanSemiring<Number>)
public operator fun <Number> Number.div(other: Number): Number = with(ring) { this@div / other }
/**
 * Returns remainder of Euclidean division (a.k.a. a division with remainder).
 *
 * A bridge contextual function for [EuclideanSemiring.rem].
 */
context(ring: EuclideanSemiring<Number>)
public operator fun <Number> Number.rem(other: Number): Number = with(ring) { this@rem % other }

/**
 * Describes a context that represents [Euclidean ring (a.k.a. Euclidean domain)](https://en.wikipedia.org/wiki/Euclidean_domain).
 * It means that it extends both [Ring] and [EuclideanSemiring] interfaces without adding anything new to them.
 * Just a composition of this two.
 */
public interface EuclideanRing<Number> : CommutativeRing<Number>, EuclideanSemiring<Number> {
    public companion object;
    
    /**
     * Registry key for [EuclideanRing] interface in [KoneContextRegistry].
     */
    @Suppliable
    public class Key<@Supply Number> : SuppliedTypeRegistryKey<EuclideanRing<Number>>() {
        override val impliedKeys: ImpliedKeysRegistry<EuclideanRing<Number>> by lazy {
            ImpliedKeysRegistry {
                CommutativeRing.Key<Number>().impliesSame()
                EuclideanSemiring.Key<Number>().impliesSame()
            }
        }
        override fun toString(): String = "dev.lounres.kone.algebraic.CommutativeRing.Key<${suppliedTypeOf<Number>()}>"
    }
}