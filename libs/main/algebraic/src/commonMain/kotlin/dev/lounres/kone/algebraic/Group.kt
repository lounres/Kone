/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.algebraic.util.doublingTimes
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


/**
 * Describes a context that represents [mathematical group](https://en.wikipedia.org/wiki/Group_(mathematics)).
 *
 * @param Number The type of elements of the group.
 */
@GenerateKoneContextKey
public interface Group<Number> : Monoid<Number> {
    // region Number-Int operations
    /**
     * The multiplication operation on a [Number] and an [Int].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val numberTimesInt: Times<Number, Int, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region Number-Long operations
    /**
     * The multiplication operation on a [Number] and an [Long].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val numberTimesLong: Times<Number, Long, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region Int-Number operations
    /**
     * The multiplication operation on an [Int] and a [Number].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val intTimesNumber: Times<Int, Number, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region Long-Number operations
    /**
     * The multiplication operation on an [Long] and a [Number].
     *
     * Default implementation uses the doubling algorithm.
     *
     * @return The multiplication context represented as [Times] instance.
     */
    @KoneContextInclude
    public val longTimesNumber: Times<Long, Number, Number> get() = Times { left, right -> left doublingTimes right }
    // endregion
    
    // region Number-Number operations
    /**
     * The negation operation on a [Number].
     *
     * @return The negation context represented as [UnaryMinus] instance.
     */
    @KoneContextInclude
    public val numberUnaryMinus: UnaryMinus<Number, Number>
    /**
     * The subtraction operation on elements of type [Number].
     *
     * @return The subtraction context represented as [Minus] instance.
     */
    @KoneContextInclude
    public val numberMinusNumber: Minus<Number, Number, Number>
    // endregion
    
    public companion object;
}

/**
 * Describes a context that represents [mathematical commutative group](https://en.wikipedia.org/wiki/Abelian_group) (a.k.a. abelian group).
 *
 * @param Number The type of elements of the group.
 */
@GenerateKoneContextKey
public interface CommutativeGroup<Number> : Group<Number>, CommutativeMonoid<Number> {
    public companion object;
}

/**
 * Type alias for [CommutativeGroup], also known as an abelian group.
 *
 * @param Number The type of elements of the abelian group.
 */
public typealias AbelianGroup<Number> = CommutativeGroup<Number>