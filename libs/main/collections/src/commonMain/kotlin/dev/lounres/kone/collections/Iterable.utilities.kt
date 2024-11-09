/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.eq


/**
 * Checks if the iterable is empty.
 */
public fun <Element> KoneIterable<Element>.isEmpty(): Boolean = size == 0u
/**
 * Checks if the iterable is not empty.
 */
public fun <Element> KoneIterable<Element>.isNotEmpty(): Boolean = !isEmpty()

/**
 * Checks if there is an element equal (in terms of the provided [Equality]) to the provided [element].
 */
context(Equality<Element>)
public operator fun <Element> KoneIterable<Element>.contains(element: Element): Boolean = any { it eq element }
/**
 * Checks if there are elements equal (in terms of the provided [Equality]) to elements from provided [collection][elements].
 */
context(Equality<Element>)
public fun <Element> KoneIterable<Element>.containsAllFrom(elements: KoneIterable<Element>): Boolean = elements.all { it in this }