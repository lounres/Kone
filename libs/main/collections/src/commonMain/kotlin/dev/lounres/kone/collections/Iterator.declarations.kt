/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


/**
 * Represents an iterator over some elements (maybe even infinite number of elements) in some (maybe temporary) order.
 *
 * In each moment it has cursor pointing either between two elements, before the first one, or after the last one.
 * If there are no elements in the collection the pointer just points nowhere.
 * All operations are made either before or after the cursor with previous or next element correspondingly.
 */
public interface KoneIterator<out Element> {
    /**
     * Returns `true` iff there is next element in the order.
     */
    public operator fun hasNext(): Boolean
    /**
     * Returns next element in the order or throws [NoFollowingElementInIteratorException] if there is no next element.
     *
     * @throws NoFollowingElementInIteratorException when there is no next element.
     */
    public fun getNext(): Element
    /**
     * Moves forward bypassing next element or throws [NoFollowingElementInIteratorException] if there is no next element.
     *
     * The previously next element becomes the previous element.
     * And the element after it becomes the next element.
     *
     * @throws NoFollowingElementInIteratorException when there is no next element.
     */
    public fun moveNext()
}

/**
 * Represents an [KoneIterator] that can iterate over the elements in both directions, forward and backward.
 */
public interface KoneReversibleIterator<out Element> : KoneIterator<Element> {
    /**
     * Returns `true` iff there is previous element in the order.
     */
    public fun hasPrevious(): Boolean
    /**
     * Returns previous element in the order or throws [NoFollowingElementInIteratorException] if there is no previous element.
     *
     * @throws NoFollowingElementInIteratorException when there is no previous element.
     */
    public fun getPrevious(): Element
    /**
     * Moves backward bypassing previous element or throws [NoFollowingElementInIteratorException] if there is no previous element.
     *
     * The previously previous element becomes the next element.
     * And the element before it becomes the previous element.
     *
     * @throws NoFollowingElementInIteratorException when there is no previous element.
     */
    public fun movePrevious()
}

/**
 * Represents a [KoneIterator] that can change value of the next element if it's present.
 */
public interface KoneSettableIterator<Element> : KoneIterator<Element> {
    /**
     * Changes the next element if it is present or throws [NoFollowingElementInIteratorException] if there is no next element.
     *
     * @throws NoFollowingElementInIteratorException when there is no next element.
     */
    public fun setNext(element: Element)
}

/**
 * Represents a [KoneReversibleIterator] that can change value of the next element and the previous element if they're present.
 */
public interface KoneReversibleSettableIterator<Element> : KoneReversibleIterator<Element>, KoneSettableIterator<Element> {
    /**
     * Changes the previous element if it is present or throws [NoFollowingElementInIteratorException] if there is no previous element.
     *
     * @throws NoFollowingElementInIteratorException when there is no previous element.
     */
    public fun setPrevious(element: Element)
}

/**
 * Represents a [KoneIterator] that can add element right after its pointer.
 */
public interface KoneExtendableIterator<Element> : KoneIterator<Element> {
    /**
     * Adds element right after the iterators pointer.
     *
     * The previous element is not changed, the provided element becomes the next element and the previously next element
     * becomes an element after the next element.
     */
    public fun addNext(element: Element)
}

/**
 * Represents a [KoneReversibleIterator] that can add element right after and right before its pointer.
 */
public interface KoneReversibleExtendableIterator<Element> : KoneReversibleIterator<Element>, KoneExtendableIterator<Element> {
    /**
     * Adds element right before the iterators pointer.
     *
     * The next element is not changed, the provided element becomes the previous element and the previously previous element
     * becomes an element before the previous element.
     */
    public fun addPrevious(element: Element)
}

/**
 * Represents a [KoneIterator] that can remove the next element if it's present.
 */
public interface KoneRemovableIterator<out Element> : KoneIterator<Element> {
    /**
     * Removes the next element if it's present or throws [NoFollowingElementInIteratorException] if there is no next element.
     *
     * @throws NoFollowingElementInIteratorException when there is no next element.
     */
    public fun removeNext()
}

/**
 * Represents a [KoneReversibleIterator] that can remove the next and the previous elements if they're present.
 */
public interface KoneReversibleRemovableIterator<out Element> : KoneReversibleIterator<Element>, KoneRemovableIterator<Element> {
    /**
     * Removes the previous element if it's present or throws [NoFollowingElementInIteratorException] if there is no previous element.
     *
     * @throws NoFollowingElementInIteratorException when there is no previous element.
     */
    public fun removePrevious()
}

/**
 * Represents a [KoneIterator] that can add, set, and remove the next element.
 */
public interface KoneMutableIterator<Element>: KoneSettableIterator<Element>, KoneExtendableIterator<Element>, KoneRemovableIterator<Element>

/**
 * Represents a [KoneReversibleIterator] that can add, set, and remove both the next and the previous elements.
 */
public interface KoneReversibleMutableIterator<Element>: KoneMutableIterator<Element>, KoneReversibleSettableIterator<Element>, KoneReversibleExtendableIterator<Element>, KoneReversibleRemovableIterator<Element>

/**
 * Represents a [KoneReversibleIterator] over some set of elements that has permanent order on the elements and permanent indices of the elements.
 *
 * In such iterators the next element is the next element in the order that has the next index among all indices of the elements.
 */
public interface KoneLinearIterator<out Element> : KoneReversibleIterator<Element> {
    /**
     * Returns index of the next element if it's present or throws [NoFollowingElementInIteratorException] if there is no next element.
     */
    public fun nextIndex(): UInt
    /**
     * Returns index of the previous element if it's present or throws [NoFollowingElementInIteratorException] if there is no previous element.
     */
    public fun previousIndex(): UInt
}

/**
 * Represents [KoneLinearIterator] and [KoneReversibleSettableIterator] at the same time.
 */
public interface KoneSettableLinearIterator<Element>: KoneLinearIterator<Element>, KoneReversibleSettableIterator<Element>

/**
 * Represents [KoneLinearIterator] and [KoneReversibleExtendableIterator] at the same time.
 */
public interface KoneExtendableLinearIterator<Element>: KoneLinearIterator<Element>, KoneReversibleExtendableIterator<Element>

/**
 * Represents [KoneLinearIterator] and [KoneReversibleRemovableIterator] at the same time.
 */
public interface KoneRemovableLinearIterator<out Element>: KoneLinearIterator<Element>, KoneReversibleRemovableIterator<Element>

/**
 * Represents [KoneLinearIterator] and [KoneReversibleMutableIterator] at the same time.
 */
public interface KoneMutableLinearIterator<Element>: KoneReversibleMutableIterator<Element>, KoneSettableLinearIterator<Element>, KoneExtendableLinearIterator<Element>, KoneRemovableLinearIterator<Element>