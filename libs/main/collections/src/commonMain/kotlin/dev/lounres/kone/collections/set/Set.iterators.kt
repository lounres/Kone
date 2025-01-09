/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.set

import dev.lounres.kone.collections.iterables.KoneIterator
import dev.lounres.kone.collections.iterables.KoneRemovableIterator
import dev.lounres.kone.collections.iterables.KoneReversibleIterator


public typealias KoneSetIterator<Element> = KoneIterator<Element>

public typealias KoneMutableSetIterator<Element> = KoneRemovableIterator<Element>

public interface KoneNoddedSetIterator<out Element> : KoneSetIterator<Element> {
    public fun getNextNode(): KoneSetNode<Element>
}

public interface KoneMutableNoddedSetIterator<out Element> : KoneNoddedSetIterator<Element>, KoneMutableSetIterator<Element> {
    override fun getNextNode(): KoneMutableSetNode<Element>
}

public typealias KoneLinkedSetIterator<Element> = KoneReversibleIterator<Element>

public typealias KoneMutableLinkedSetIterator<Element> = KoneReversibleIterator<Element>

public interface KoneLinkedNoddedSetIterator<out Element> : KoneLinkedSetIterator<Element>, KoneNoddedSetIterator<Element> {
    override fun getNextNode(): KoneLinkedSetNode<Element>
    public fun getPreviousNode(): KoneLinkedSetNode<Element>
}

public interface KoneMutableLinkedNoddedSetIterator<out Element> : KoneLinkedNoddedSetIterator<Element>, KoneMutableLinkedSetIterator<Element>, KoneMutableNoddedSetIterator<Element> {
    override fun getNextNode(): KoneMutableLinkedSetNode<Element>
    override fun getPreviousNode(): KoneMutableLinkedSetNode<Element>
}