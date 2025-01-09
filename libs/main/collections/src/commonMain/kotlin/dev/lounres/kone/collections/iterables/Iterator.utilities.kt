/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterables


/**
 * Gets the next element, moves forward, and returns the got element.
 */
public fun <Element> KoneIterator<Element>.getAndMoveNext(): Element = getNext().also { moveNext() }

/**
 * Gets the next element moves forward and returns the got element. It is an operator function to use in `for` cycles.
 */
@Suppress("NOTHING_TO_INLINE")
public inline operator fun <Element> KoneIterator<Element>.next(): Element = getAndMoveNext()

/**
 * Gets the previous element, moves backward, and returns the got element.
 */
public fun <Element> KoneReversibleIterator<Element>.getAndMovePrevious(): Element = getPrevious().also { movePrevious() }