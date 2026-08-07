/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneSeveralElementsInserter<in Element> {
    public val newElementsNumber: UInt
    public fun insert(element: Element)
    public fun close()
}

public interface KoneBulkElementsRemover<out Element> {
    public fun hasNext(): Boolean
    public fun getNext(): Element
    public fun nextIndex(): UInt
    public fun moveNext()
    public fun removeNext()
    public fun close()
}