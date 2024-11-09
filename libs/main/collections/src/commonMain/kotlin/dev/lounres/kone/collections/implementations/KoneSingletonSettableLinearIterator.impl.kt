/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneSettableLinearIterator
import dev.lounres.kone.collections.indexOutOfBoundsException


internal class KoneSingletonSettableLinearIterator<Element>(
    var singleElement: Element,
    var currentlyBeforeSingleElement: Boolean = true
): KoneSettableLinearIterator<Element> {
    override fun hasNext(): Boolean = currentlyBeforeSingleElement
    override fun getNext(): Element {
        if (!hasNext()) indexOutOfBoundsException(1u, 1u)
        return singleElement
    }
    override fun moveNext() {
        if (!hasNext()) indexOutOfBoundsException(1u, 1u)
        currentlyBeforeSingleElement = false
    }
    override fun nextIndex(): UInt = if (hasNext()) 1u else indexOutOfBoundsException(1u, 1u)
    override fun setNext(element: Element) {
        if (!hasNext()) indexOutOfBoundsException(1u, 1u)
        singleElement = element
    }

    override fun hasPrevious(): Boolean = !currentlyBeforeSingleElement
    override fun getPrevious(): Element {
        if (!hasPrevious()) indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
        return singleElement
    }
    override fun movePrevious() {
        if (!hasPrevious()) indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
        currentlyBeforeSingleElement = true
    }
    override fun previousIndex(): UInt = if (hasPrevious()) 0u else indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
    override fun setPrevious(element: Element) {
        if (!hasPrevious()) indexOutOfBoundsException(UInt.MAX_VALUE, 1u)
        singleElement = element
    }
}