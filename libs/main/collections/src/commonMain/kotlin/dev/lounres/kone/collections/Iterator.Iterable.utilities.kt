/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.eq


context(Equality<Element>)
public operator fun <Element> KoneIterable<Element>.contains(element: Element): Boolean = any { it eq element }
context(Equality<Element>)
public fun <Element> KoneIterable<Element>.containsAllFrom(elements: Iterable<Element>): Boolean = elements.all { it in this }