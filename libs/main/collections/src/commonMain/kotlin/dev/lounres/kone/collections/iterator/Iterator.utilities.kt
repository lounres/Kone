/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterator


/**
 * Gets the next element in [this] iterator, moves forward, and returns the got element.
 *
 * @receiver The used iterator.
 * @param Element The type of elements in the iterator.
 * @return The next element that the operation steps over.
 */
public fun <Element> KoneIterator<Element>.getAndMoveNext(): Element = getNext().also { moveNext() }

/**
 * Gets the next element moves forward and returns the got element. It is an operator function to use in `for` cycles.
 *
 * @receiver The used iterator.
 * @param Element The type of elements in the iterator.
 * @return The next element that the operation steps over.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun <Element> KoneIterator<Element>.next(): Element = getAndMoveNext()

/**
 * Gets the previous element, moves backward, and returns the got element.
 *
 * @receiver The used iterator.
 * @param Element The type of elements in the iterator.
 * @return The previous element that the operation steps over.
 */
public fun <Element> KoneReversibleIterator<Element>.getAndMovePrevious(): Element = getPrevious().also { movePrevious() }