/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterator
import dev.lounres.kone.collections.indexException


internal class SingletonLinearIterator<Element>(
    val singleElement: Element,
    var currentlyBeforeSingleElement: Boolean = true
): KoneLinearIterator<Element> {
    override fun hasNext(): Boolean = currentlyBeforeSingleElement
    override fun getNext(): Element {
        if (!hasNext()) indexException(1u, 1u)
        return singleElement
    }
    override fun moveNext() {
        if (!hasNext()) indexException(1u, 1u)
        currentlyBeforeSingleElement = false
    }
    override fun nextIndex(): UInt = if (hasNext()) 1u else indexException(1u, 1u)

    override fun hasPrevious(): Boolean = !currentlyBeforeSingleElement
    override fun getPrevious(): Element {
        if (!hasPrevious()) indexException(UInt.MAX_VALUE, 1u)
        return singleElement
    }
    override fun movePrevious() {
        if (!hasPrevious()) indexException(UInt.MAX_VALUE, 1u)
        currentlyBeforeSingleElement = true
    }
    override fun previousIndex(): UInt = if (hasPrevious()) 0u else indexException(UInt.MAX_VALUE, 1u)
}