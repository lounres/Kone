/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set.empty

import dev.lounres.kone.collections.*
import dev.lounres.kone.collections.set.KoneNoddedSetIterator
import dev.lounres.kone.collections.set.KoneSetNode


internal object KoneEmptyNoddedSetIterator: KoneNoddedSetIterator<Nothing> {
    override fun hasNext(): Boolean = false
    override fun getNext(): Nothing = noNextElementInIteratorException()
    override fun getNextNode(): KoneSetNode<Nothing> = noNextElementInIteratorException()
    override fun moveNext() = noNextElementInIteratorException()
    
    override fun equals(other: Any?): Boolean = this === other
    override fun hashCode(): Int = super.hashCode()
    override fun toString(): String = super.toString()
}