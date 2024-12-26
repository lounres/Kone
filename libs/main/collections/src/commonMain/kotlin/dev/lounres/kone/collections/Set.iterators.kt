/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public typealias KoneSetIterator<Element> = KoneIterator<Element>
public typealias KoneMutableSetIterator<Element> = KoneRemovableIterator<Element>

public interface KoneNoddedSetIterator<out Element> : KoneSetIterator<Element> {
    public fun getNextNode(): KoneSetNode<Element>
}

public interface KoneMutableNoddedSetIterator<out Element> : KoneNoddedSetIterator<Element>, KoneMutableSetIterator<Element> {
    override fun getNextNode(): KoneMutableSetNode<Element>
}