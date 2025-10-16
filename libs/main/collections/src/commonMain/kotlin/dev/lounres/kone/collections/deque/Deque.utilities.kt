/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque

import dev.lounres.kone.collections.EmptyDequeAccessException
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next


/**
 * Checks if the collection is empty.
 */
public fun KoneDeque<*>.isEmpty(): Boolean = size == 0u
/**
 * Checks if the collection is not empty.
 */
public fun KoneDeque<*>.isNotEmpty(): Boolean = !isEmpty()

/**
 * Returns and removes the first element.
 * The element was placed at the beginning of the sequence.
 *
 * If there is no element in the collection, [EmptyDequeAccessException] is thrown.
 *
 * @throws EmptyDequeAccessException when the collection is empty.
 */
public fun <Element> KoneDeque<out Element>.popFirst(): Element = getFirst().also { removeFirst() }
/**
 * Returns and removes the last element.
 * The element was placed at the end of the sequence.
 *
 * If there is no element in the collection, [EmptyDequeAccessException] is thrown.
 *
 * @throws EmptyDequeAccessException when the collection is empty.
 */
public fun <Element> KoneDeque<out Element>.popLast(): Element = getLast().also { removeLast() }

public fun <Element> KoneDeque<in Element>.addLastAllFrom(iterable: KoneIterable<Element>) {
    for (element in iterable) addLast(element)
}