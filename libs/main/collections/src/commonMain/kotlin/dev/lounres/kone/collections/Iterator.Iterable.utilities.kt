/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.eq


context(Equality<E>)
public operator fun <E> KoneIterable<E>.contains(element: E): Boolean = any { it eq element }
context(Equality<E>)
public fun <E> KoneIterable<E>.containsAllFrom(elements: Iterable<E>): Boolean = elements.all { it in this }