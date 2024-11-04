/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneDequeue<E> {
    public val size: UInt
    
    public fun getFirst(): E
    public fun getLast(): E
    public fun addFirst(element: E)
    public fun addLast(element: E)
    public fun removeFirst()
    public fun removeLast()
}