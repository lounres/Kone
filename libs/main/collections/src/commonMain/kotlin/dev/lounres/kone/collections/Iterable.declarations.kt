/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


/**
 * Represents a finite collection of elements with described size and iteration over it.
 */
public interface KoneIterable<out Element> {
    /**
     * Number of elements in the collection.
     */
    public val size: UInt
    /**
     * Iterator over elements of the collection. There must be exactly [size] number of elements in it.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     */
    public operator fun iterator(): KoneIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleIterator].
 */
public interface KoneReversibleIterable<out Element>: KoneIterable<Element> {
    public override operator fun iterator(): KoneReversibleIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneSettableIterator].
 */
public interface KoneSettableIterable<Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneSettableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleSettableIterator].
 */
public interface KoneReversibleSettableIterable<Element> : KoneReversibleIterable<Element>, KoneSettableIterable<Element> {
    public override operator fun iterator(): KoneReversibleSettableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneExtendableIterator].
 */
public interface KoneExtendableIterable<Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneExtendableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleExtendableIterator].
 */
public interface KoneReversibleExtendableIterable<Element> : KoneReversibleIterable<Element>, KoneExtendableIterable<Element> {
    public override operator fun iterator(): KoneReversibleExtendableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneRemovableIterator].
 */
public interface KoneRemovableIterable<out Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneRemovableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleRemovableIterator].
 */
public interface KoneReversibleRemovableIterable<out Element> : KoneReversibleIterable<Element>, KoneRemovableIterable<Element> {
    public override operator fun iterator(): KoneReversibleRemovableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneMutableIterator].
 */
public interface KoneMutableIterable<Element> : KoneSettableIterable<Element>, KoneExtendableIterable<Element>, KoneRemovableIterable<Element> {
    public override operator fun iterator(): KoneMutableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleMutableIterator].
 */
public interface KoneReversibleMutableIterable<Element> : KoneMutableIterable<Element>, KoneReversibleSettableIterable<Element>, KoneReversibleExtendableIterable<Element>, KoneReversibleRemovableIterable<Element> {
    public override operator fun iterator(): KoneReversibleMutableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneLinearIterator].
 */
public interface KoneLinearIterable<out Element> : KoneReversibleIterable<Element> {
    public override operator fun iterator(): KoneLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneSettableLinearIterator].
 */
public interface KoneSettableLinearIterable<Element> : KoneLinearIterable<Element>, KoneReversibleSettableIterable<Element> {
    public override operator fun iterator(): KoneSettableLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneExtendableLinearIterator].
 */
public interface KoneExtendableLinearIterable<Element> : KoneLinearIterable<Element>, KoneReversibleExtendableIterable<Element> {
    public override operator fun iterator(): KoneExtendableLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneRemovableLinearIterator].
 */
public interface KoneRemovableLinearIterable<out Element> : KoneLinearIterable<Element>, KoneReversibleRemovableIterable<Element> {
    public override operator fun iterator(): KoneRemovableLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneMutableLinearIterator].
 */
public interface KoneMutableLinearIterable<Element> : KoneSettableLinearIterable<Element>, KoneExtendableLinearIterable<Element>, KoneRemovableLinearIterable<Element>, KoneReversibleMutableIterable<Element> {
    public override operator fun iterator(): KoneMutableLinearIterator<Element>
}