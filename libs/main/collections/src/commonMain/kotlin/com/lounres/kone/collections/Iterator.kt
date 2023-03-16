/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections


public interface KoneIterator<out E> {
    public operator fun next(): E
    public operator fun hasNext(): Boolean
}

public interface KoneMutableIterator<E> : KoneIterator<E> {
    public fun remove()
    public fun add(element: E)
}

public interface KoneSettableIterator<E> : KoneMutableIterator<E> {
    public fun set(element: E)
}

public interface KoneLinearIterator<out E> : KoneIterator<E> {
    public override operator fun next(): E
    public override operator fun hasNext(): Boolean
    public fun nextIndex(): UInt

    public fun hasPrevious(): Boolean
    public fun previous(): E
    public fun previousIndex(): UInt
}

public interface KoneMutableSetIterator<E>: KoneMutableIterator<E>

public interface KoneMutableLinearIterator<E>: KoneLinearIterator<E>, KoneSettableIterator<E>