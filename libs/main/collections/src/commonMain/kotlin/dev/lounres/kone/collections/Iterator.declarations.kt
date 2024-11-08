/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public interface KoneIterator<out Element> {
    public operator fun hasNext(): Boolean
    public fun getNext(): Element
    public fun moveNext()
}

public interface KoneReversibleIterator<out Element> : KoneIterator<Element> {
    public fun hasPrevious(): Boolean
    public fun getPrevious(): Element
    public fun movePrevious()
}

public interface KoneSettableIterator<Element> : KoneIterator<Element> {
    public fun setNext(element: Element)
}

public interface KoneReversibleSettableIterator<Element> : KoneReversibleIterator<Element>, KoneSettableIterator<Element> {
    public fun setPrevious(element: Element)
}

public interface KoneExtendableIterator<Element> : KoneIterator<Element> {
    public fun addNext(element: Element)
}

public interface KoneReversibleExtendableIterator<Element> : KoneReversibleIterator<Element>, KoneExtendableIterator<Element> {
    public fun addPrevious(element: Element)
}

public interface KoneRemovableIterator<out Element> : KoneIterator<Element> {
    public fun removeNext()
}

public interface KoneReversibleRemovableIterator<out Element> : KoneReversibleIterator<Element>, KoneRemovableIterator<Element> {
    public fun removePrevious()
}

public interface KoneMutableIterator<Element>: KoneSettableIterator<Element>, KoneExtendableIterator<Element>, KoneRemovableIterator<Element>

public interface KoneReversibleMutableIterator<Element>: KoneMutableIterator<Element>, KoneReversibleSettableIterator<Element>, KoneReversibleExtendableIterator<Element>, KoneReversibleRemovableIterator<Element>

public interface KoneLinearIterator<out Element> : KoneReversibleIterator<Element> {
    public fun nextIndex(): UInt
    public fun previousIndex(): UInt
}

public interface KoneSettableLinearIterator<Element>: KoneLinearIterator<Element>, KoneReversibleSettableIterator<Element>

public interface KoneExtendableLinearIterator<Element>: KoneLinearIterator<Element>, KoneReversibleExtendableIterator<Element>

public interface KoneRemovableLinearIterator<out Element>: KoneLinearIterator<Element>, KoneReversibleRemovableIterator<Element>

public interface KoneMutableLinearIterator<Element>: KoneReversibleMutableIterator<Element>, KoneSettableLinearIterator<Element>, KoneExtendableLinearIterator<Element>, KoneRemovableLinearIterator<Element>