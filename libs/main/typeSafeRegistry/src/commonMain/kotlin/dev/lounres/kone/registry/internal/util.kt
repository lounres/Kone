/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry.internal


/**
 * A singleton empty iterator that always returns `false` for [hasNext] and throws for [next].
 * 
 * Used as a default implementation for empty iterables in the registry system.
 */
internal object EmptyIterator : Iterator<Nothing> {
    override fun hasNext(): Boolean = false
    override fun next(): Nothing = throw NoSuchElementException("Empty iterator does not contain anything")
}

/**
 * A singleton empty iterable that always returns [EmptyIterator].
 * 
 * Used as a default implementation for empty collections in the registry system.
 */
internal object EmptyIterable : Iterable<Nothing> {
    override fun iterator(): Iterator<Nothing> = EmptyIterator
}