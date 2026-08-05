/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterable

import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.eq


/**
 * Checks if the iterable is empty.
 *
 * @receiver The iterable which emptiness is checked.
 * @return `true` is the iterable is empty, `false` otherwise.
 */
public fun KoneIterable<*>.isEmpty(): Boolean = size == 0u
/**
 * Checks if the iterable is not empty.
 *
 * @receiver The iterable which non-emptiness is checked.
 * @return `true` is the iterable is not empty, `false` otherwise.
 */
public fun KoneIterable<*>.isNotEmpty(): Boolean = !isEmpty()

/**
 * Checks if there is an element equal (in terms of the provided [Equality]) to the provided [element].
 *
 * The function goes through the whole iterable and stops iff there is no next element (when `false` is returned)
 * or it found an equal element (when `true` is returned).
 *
 * @param Element The type of elements.
 * @receiver The [Equality] context used to compare elements.
 * @param element The element to search for.
 * @return `true` if the iterable contains the element, `false` otherwise.
 */
context(_: Equality<Element>)
public operator fun <Element> KoneIterable<Element>.contains(element: Element): Boolean = any { it eq element }

/**
 * Checks if there are elements equal (in terms of the provided [Equality]) to elements from provided [elements].
 *
 * The function runs [KoneIterable.contains] for each in the [elements] iterable.
 *
 * @param Element The type of elements.
 * @receiver The [Equality] context used to compare elements.
 * @param elements The iterable of elements to check for containment.
 * @return `true` if all elements from [elements] are contained in this iterable, `false` otherwise.
 */
context(_: Equality<Element>)
public fun <Element> KoneIterable<Element>.containsAllFrom(elements: KoneIterable<Element>): Boolean = elements.all { it in this }