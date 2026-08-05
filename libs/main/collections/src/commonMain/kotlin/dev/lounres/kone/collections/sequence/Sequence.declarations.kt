/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.sequence

import dev.lounres.kone.collections.iterator.KoneIterator


/**
 * Represents a sequence of elements that can be accessed via its [iterator].
 *
 * Usually the sequences are lazily evaluated and evaluated values are not cached.
 * So you should think of them as a formal instance that can produce some values one-by-one.
 * Also, there is no saying if it is finite or not.
 *
 * Standard examples are either lazily evaluated finite sequence of elements like lazy mapping or filtering of an iterable
 * or fixed infinite sequences (which, obviously, cannot be evaluated and stored anywhere in a fixed time) like Fibonacci numbers.
 *
 * If you need to cache your values, so that the sequence won't be "reevaluated" again, there is [KoneSequence.cached].
 *
 * @param Element The type of elements in the sequence.
 */
public fun interface KoneSequence<out Element> {
    /**
     * Returns an iterator over the elements of this sequence.
     *
     * @return An iterator over the elements.
     */
    public operator fun iterator(): KoneIterator<Element>
    
    public companion object
}