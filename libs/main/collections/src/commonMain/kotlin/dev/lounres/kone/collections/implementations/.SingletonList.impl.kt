/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


internal class SingletonList<Element>(
    val singleElement: Element,
) : KoneList<Element> {
    override val size: UInt = 1u

    override fun get(index: UInt): Element {
        if (index >= 1u) indexException(index, size)
        return singleElement
    }
    
    override fun iterator(): KoneLinearIterator<Element> = SingletonLinearIterator(singleElement = singleElement)
    override fun iteratorFrom(index: UInt): KoneLinearIterator<Element> =
        when(index) {
            0u -> SingletonLinearIterator(singleElement = singleElement)
            1u -> SingletonLinearIterator(singleElement = singleElement).apply { moveNext() }
            else -> indexException(index, size)
        }

    override fun toString(): String = "[$singleElement]"
    override fun hashCode(): Int = 31 + singleElement.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (other.size != 1u) return false

        return singleElement == other[0u]
    }
}