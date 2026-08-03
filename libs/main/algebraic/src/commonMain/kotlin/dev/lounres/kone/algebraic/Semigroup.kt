/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.algebraic

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextInclude
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey


/**
 * Describes a context that represents [mathematical semigroup](https://en.wikipedia.org/wiki/Semigroup).
 *
 * @param Number The type of elements of the semigroup.
 */
@GenerateKoneContextKey
public interface Semigroup<Number> : KoneContext {
    // region Number-Number operations
    /**
     * The associative binary addition operation on elements of type [Number].
     *
     * @return The addition context represented as [Plus] instance.
     */
    @KoneContextInclude
    public val numberPlusNumber: Plus<Number, Number, Number>
    // endregion
    
    public companion object;
}

/**
 * Describes a context that represents [mathematical commutative semigroup](https://en.wikipedia.org/wiki/Semigroup) (a.k.a. abelian semigroup).
 *
 * @param Number The type of elements of the commutative semigroup.
 */
@GenerateKoneContextKey
public interface CommutativeSemigroup<Number> : Semigroup<Number> {
    public companion object;
}