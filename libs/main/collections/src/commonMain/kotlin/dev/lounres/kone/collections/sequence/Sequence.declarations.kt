/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.sequence

import dev.lounres.kone.collections.iterator.KoneIterator


/**
 * Represents a lazily evaluated sequence of elements.
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
    
    /**
     * Companion object for [KoneSequence].
     */
    public companion object
}