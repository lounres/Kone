/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneIterator<out E> {
    public operator fun hasNext(): Boolean
    public fun getNext(): E
    public fun moveNext()
}

public operator fun <E> KoneIterator<E>.next(): E = getAndMoveNext()

public interface KoneReversibleIterator<out E> : KoneIterator<E> {
    public fun hasPrevious(): Boolean
    public fun getPrevious(): E
    public fun movePrevious()
}

public interface KoneSettableIterator<E> : KoneIterator<E> {
    public fun setNext(element: E)
}

public interface KoneReversibleSettableIterator<E> : KoneReversibleIterator<E>, KoneSettableIterator<E> {
    public fun setPrevious(element: E)
}

public interface KoneExtendableIterator<E> : KoneIterator<E> {
    public fun addNext(element: E)
}

public interface KoneReversibleExtendableIterator<E> : KoneReversibleIterator<E>, KoneExtendableIterator<E> {
    public fun addPrevious(element: E)
}

public interface KoneRemovableIterator<out E> : KoneIterator<E> {
    public fun removeNext()
}

public interface KoneReversibleRemovableIterator<out E> : KoneReversibleIterator<E>, KoneRemovableIterator<E> {
    public fun removePrevious()
}

public interface KoneMutableIterator<E>: KoneSettableIterator<E>, KoneExtendableIterator<E>, KoneRemovableIterator<E>

public interface KoneReversibleMutableIterator<E>: KoneMutableIterator<E>, KoneReversibleSettableIterator<E>, KoneReversibleExtendableIterator<E>, KoneReversibleRemovableIterator<E>

public interface KoneLinearIterator<out E> : KoneReversibleIterator<E> {
    public fun nextIndex(): UInt
    public fun previousIndex(): UInt
}

public interface KoneSettableLinearIterator<E>: KoneLinearIterator<E>, KoneReversibleSettableIterator<E>

public interface KoneExtendableLinearIterator<E>: KoneLinearIterator<E>, KoneReversibleExtendableIterator<E>

public interface KoneRemovableLinearIterator<out E>: KoneLinearIterator<E>, KoneReversibleRemovableIterator<E>

public interface KoneMutableLinearIterator<E>: KoneReversibleMutableIterator<E>, KoneSettableLinearIterator<E>, KoneExtendableLinearIterator<E>, KoneRemovableLinearIterator<E>