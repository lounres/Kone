/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneIterator<out E> {
    public operator fun hasNext(): Boolean
    public operator fun next(): E
}

public interface KoneSettableIterator<E> : KoneIterator<E> {
    public fun set(element: E)
}

public interface KoneExtendableIterator<E> : KoneIterator<E> {
    public fun add(element: E)
}

public interface KoneRemovableIterator<out E> : KoneIterator<E> {
    public fun remove()
}

public interface KoneMutableIterator<E>: KoneSettableIterator<E>, KoneExtendableIterator<E>, KoneRemovableIterator<E>

public interface KoneLinearIterator<out E> : KoneIterator<E> {
    public override operator fun hasNext(): Boolean
    public override operator fun next(): E
    public fun nextIndex(): UInt

    public fun hasPrevious(): Boolean
    public fun previous(): E
    public fun previousIndex(): UInt
}

public interface KoneSettableLinearIterator<E>: KoneLinearIterator<E>, KoneSettableIterator<E>

public interface KoneExtendableLinearIterator<E>: KoneLinearIterator<E>, KoneExtendableIterator<E>

public interface KoneRemovableLinearIterator<out E>: KoneLinearIterator<E>, KoneRemovableIterator<E>

public interface KoneMutableLinearIterator<E>: KoneMutableIterator<E>, KoneSettableLinearIterator<E>, KoneExtendableLinearIterator<E>, KoneRemovableLinearIterator<E>