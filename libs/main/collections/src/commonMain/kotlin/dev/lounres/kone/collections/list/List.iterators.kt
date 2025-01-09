/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.list

import dev.lounres.kone.collections.iterables.KoneLinearIterator
import dev.lounres.kone.collections.iterables.KoneMutableLinearIterator
import dev.lounres.kone.collections.iterables.KoneSettableLinearIterator


public typealias KoneListIterator<Element> = KoneLinearIterator<Element>
public typealias KoneSettableListIterator<Element> = KoneSettableLinearIterator<Element>
public typealias KoneMutableListIterator<Element> = KoneMutableLinearIterator<Element>

public interface KoneNoddedListIterator<out Element> : KoneListIterator<Element> {
    public fun getNextNode(): KoneListNode<Element>
    public fun getPreviousNode(): KoneListNode<Element>
}

public interface KoneSettableNoddedListIterator<Element> : KoneNoddedListIterator<Element>, KoneSettableListIterator<Element> {
    override fun getNextNode(): KoneSettableListNode<Element>
    override fun getPreviousNode(): KoneSettableListNode<Element>
}

public interface KoneMutableNoddedListIterator<Element> : KoneSettableNoddedListIterator<Element>, KoneMutableListIterator<Element> {
    override fun getNextNode(): KoneMutableListNode<Element>
    override fun getPreviousNode(): KoneMutableListNode<Element>
}