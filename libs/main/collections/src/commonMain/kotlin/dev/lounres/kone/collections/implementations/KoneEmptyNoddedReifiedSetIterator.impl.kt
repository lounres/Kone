/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.*


internal object KoneEmptyNoddedSetIterator: KoneNoddedSetIterator<Nothing> {
    override fun hasNext(): Boolean = false
    override fun getNext(): Nothing = noNextElementInIteratorException()
    override fun getNextNode(): KoneSetNode<Nothing> = noNextElementInIteratorException()
    override fun moveNext() = noNextElementInIteratorException()
}