/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


/**
 * Describes a context that represents [mathematical monoid](https://en.wikipedia.org/wiki/Monoid).
 *
 * @param Number The type of elements of the monoid.
 */
@GenerateKoneContextKey
public interface Monoid<Number> : Semigroup<Number> {
    // region Constants
    /**
     * The neutral additive element (a.k.a. *zero element*) of the monoid.
     */
    public val zero: Number
    // endregion
    
    // region Number-UInt operations
    /**
     * The multiplication operation on a [Number] and an [UInt].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val numberTimesUInt: Times<Number, UInt, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region Number-ULong operations
    /**
     * The multiplication operation on a [Number] and an [ULong].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val numberTimesULong: Times<Number, ULong, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region UInt-Number operations
    /**
     * The multiplication operation on an [UInt] and a [Number].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val uIntTimesNumber: Times<UInt, Number, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region ULong-Number operations
    /**
     * The multiplication operation on an [ULong] and a [Number].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val uLongTimesNumber: Times<ULong, Number, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    public companion object;
}

/**
 * Describes a context that represents [mathematical commutative monoid](https://en.wikipedia.org/wiki/Monoid) (a.k.a. abelian monoid).
 *
 * @param Number The type of elements of the commutative monoid.
 */
@GenerateKoneContextKey
public interface CommutativeMonoid<Number> : Monoid<Number>, CommutativeSemigroup<Number> {
    public companion object;
}