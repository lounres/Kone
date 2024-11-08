/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


public fun <Element> KoneList<Element>.getOrNull(index: UInt): Element? = if (index < size) this[index] else null
public fun <Element> KoneList<Element>.getMaybe(index: UInt): Option<Element> = if (index < size) Some(this[index]) else None

public fun <Element> KoneMutableList<Element>.addAllFrom(elements: KoneIterable<Element>) {
    val iterator = iterator()
    addSeveral(elements.size) { iterator.getAndMoveNext() }
}

public fun <Element> KoneMutableList<Element>.addAllFromAt(index: UInt, elements: KoneIterable<Element>) {
    val iterator = iterator()
    addSeveralAt(index, elements.size) { iterator.getAndMoveNext() }
}

public val KoneList<*>.lastIndex: UInt get() = size - 1u
public val KoneList<*>.indices: UIntRange get() = 0u ..< size