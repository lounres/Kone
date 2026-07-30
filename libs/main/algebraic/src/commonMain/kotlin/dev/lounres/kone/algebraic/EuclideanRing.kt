/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.registry.ImpliedKeysRegistry
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf
import kotlinx.serialization.Serializable


/**
 * Wrapper class for a result of Euclidean division (a.k.a. a division with remainder).
 * See [EuclideanSemiring]'s or [EuclideanRing]'s docs for more.
 *
 * @param Number The type of elements being divided.
 * @param quotient The quotient of the division.
 * @param remainder The remainder of the division.
 */
@Serializable
//@JvmInline // There might be a problem with the MFVC and context parameters. See KT-72538 for more.
public /*value*/ data class EuclideanDivisionResult<Number>(public val quotient: Number, public val remainder: Number) {
//    public operator fun component1(): Number = quotient
//    public operator fun component2(): Number = remainder
}

/**
 * Describes a context that represents Euclidean semiring
 * (i.e. [Euclidean ring](https://en.wikipedia.org/wiki/Euclidean_domain) (a.k.a. Euclidean domain) but without subtraction).
 *
 * > **Note!**
 * Euclidean norm is not provided by the interface.
 * It means that only the Euclidean division operations are provided
 * mentioning that *there is* some Euclidean norm, so that the operations satisfy conditions on it,
 * but no actual programming representation of the norm is provided.
 *
 * @param Number The type of elements of the Euclidean semiring.
 */
public interface EuclideanSemiring<Number> : CommutativeSemiring<Number> {
    /**
     * The Euclidean division (division with remainder) operation on elements of type [Number] with [EuclideanDivisionResult] as result.
     *
     * @return The division context represented as [DivideRemainder] instance.
     */
    @KoneContextInclude
    public val numberDivideRemainderNumber: DivideRemainder<Number, Number, EuclideanDivisionResult<Number>>
    /**
     * The Euclidean division (division with remainder) operation on elements of type [Number] with quotient as result.
     *
     * Default implementation uses resulting quotient of [DivideRemainder] operation.
     *
     * @return The division context represented as [Divide] instance.
     */
    @KoneContextInclude
    public val numberDivideNumber: Divide<Number, Number, Number> get() = Divide { left, right -> numberDivideRemainderNumber { left divrem right }.quotient }
    /**
     * The Euclidean division (division with remainder) operation on elements of type [Number] with remainder as result.
     *
     * Default implementation uses resulting remainder of [DivideRemainder] operation.
     *
     * @return The division context represented as [Remainder] instance.
     */
    @KoneContextInclude
    public val numberRemainderNumber: Remainder<Number, Number, Number> get() = Remainder { left, right -> numberDivideRemainderNumber { left divrem right }.remainder }
    
    public companion object;
    
    /**
     * Registry key for [EuclideanSemiring] interface in [Registry].
     *
     * @param Number The type of elements of the Euclidean semiring.
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
 * Describes a context that represents [Euclidean ring](https://en.wikipedia.org/wiki/Euclidean_domain) (a.k.a. Euclidean domain).
 *
 * > **Note!**
 * Euclidean norm is not provided by the interface.
 * It means that only the Euclidean division operations are provided
 * mentioning that *there is* some Euclidean norm, so that the operations satisfy conditions on it,
 * but no actual programming representation of the norm is provided.
 *
 * @param Number The type of elements of the Euclidean semiring.
 */
public interface EuclideanRing<Number> : CommutativeRing<Number>, EuclideanSemiring<Number> {
    public companion object;
    
    /**
     * Registry key for [EuclideanRing] interface in [Registry].
     *
     * @param Number The type of elements of the Euclidean ring.
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