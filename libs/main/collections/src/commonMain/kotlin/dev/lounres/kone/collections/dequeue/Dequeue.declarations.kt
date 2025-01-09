/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.dequeue


public interface KoneDequeue<Element> {
    public val size: UInt
    
    public fun getFirst(): Element
    public fun getLast(): Element
    public fun addFirst(element: Element)
    public fun addLast(element: Element)
    public fun removeFirst()
    public fun removeLast()
}