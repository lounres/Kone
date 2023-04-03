/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections


public interface KoneIterator<out E> {
    public operator fun next(): E
    public operator fun hasNext(): Boolean
}

public interface KoneExtendableIterator<E> : KoneIterator<E> {
    public fun add(element: E)
}

public interface KoneRemovableIterator<out E> : KoneIterator<E> {
    public fun remove()
}

public interface KoneMutableIterator<E>: KoneRemovableIterator<E>, KoneExtendableIterator<E> {
    public fun set(element: E)
}

public interface KoneLinearIterator<out E> : KoneIterator<E> {
    public override operator fun hasNext(): Boolean
    public override operator fun next(): E
    public fun nextIndex(): UInt

    public fun hasPrevious(): Boolean
    public fun previous(): E
    public fun previousIndex(): UInt
}

public interface KoneExtendableLinearIterator<E>: KoneLinearIterator<E>, KoneExtendableIterator<E>

public interface KoneRemovableLinearIterator<out E>: KoneLinearIterator<E>, KoneRemovableIterator<E>

public interface KoneMutableLinearIterator<E>: KoneRemovableLinearIterator<E>, KoneExtendableLinearIterator<E>, KoneMutableIterator<E>