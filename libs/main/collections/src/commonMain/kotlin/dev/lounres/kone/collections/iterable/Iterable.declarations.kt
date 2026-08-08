/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterable

import dev.lounres.kone.collections.iterable.serializers.*
import dev.lounres.kone.collections.iterator.*
import kotlinx.serialization.Serializable


/**
 * Represents a finite collection of elements with described size and iteration over it.
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneIterableSerializer::class)
public interface KoneIterable<out Element> {
    /**
     * Number of elements in the collection.
     *
     * @return The size of the iterable.
     */
    public val size: UInt
    /**
     * Checks if the iterable is empty.
     *
     * @return `true` is the iterable is empty, `false` otherwise.
     */
    public fun isEmpty(): Boolean = size == 0u
    /**
     * Iterator over elements of the collection. There must be exactly [size] number of elements in it.
     *
     * Also, iterator should not be used after the underlying structure of the collection is changed not by the iterator.
     *
     * @return The iterator walking through the iterable.
     */
    public operator fun iterator(): KoneIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneReversibleIterableSerializer::class)
public interface KoneReversibleIterable<out Element>: KoneIterable<Element> {
    public override operator fun iterator(): KoneReversibleIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneSettableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneSettableIterableSerializer::class)
public interface KoneSettableIterable<Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneSettableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleSettableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneReversibleSettableIterableSerializer::class)
public interface KoneReversibleSettableIterable<Element> : KoneReversibleIterable<Element>, KoneSettableIterable<Element> {
    public override operator fun iterator(): KoneReversibleSettableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneExtendableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneExtendableIterableSerializer::class)
public interface KoneExtendableIterable<Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneExtendableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleExtendableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneReversibleExtendableIterableSerializer::class)
public interface KoneReversibleExtendableIterable<Element> : KoneReversibleIterable<Element>, KoneExtendableIterable<Element> {
    public override operator fun iterator(): KoneReversibleExtendableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneRemovableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneRemovableIterableSerializer::class)
public interface KoneRemovableIterable<out Element> : KoneIterable<Element> {
    public override operator fun iterator(): KoneRemovableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleRemovableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneReversibleRemovableIterableSerializer::class)
public interface KoneReversibleRemovableIterable<out Element> : KoneReversibleIterable<Element>, KoneRemovableIterable<Element> {
    public override operator fun iterator(): KoneReversibleRemovableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneMutableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneMutableIterableSerializer::class)
public interface KoneMutableIterable<Element> : KoneSettableIterable<Element>, KoneExtendableIterable<Element>, KoneRemovableIterable<Element> {
    public override operator fun iterator(): KoneMutableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneReversibleMutableIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneReversibleMutableIterableSerializer::class)
public interface KoneReversibleMutableIterable<Element> : KoneMutableIterable<Element>, KoneReversibleSettableIterable<Element>, KoneReversibleExtendableIterable<Element>, KoneReversibleRemovableIterable<Element> {
    public override operator fun iterator(): KoneReversibleMutableIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneLinearIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneLinearIterableSerializer::class)
public interface KoneLinearIterable<out Element> : KoneReversibleIterable<Element> {
    public override operator fun iterator(): KoneLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneSettableLinearIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneSettableLinearMutableIterableSerializer::class)
public interface KoneSettableLinearIterable<Element> : KoneLinearIterable<Element>, KoneReversibleSettableIterable<Element> {
    public override operator fun iterator(): KoneSettableLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneExtendableLinearIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneExtendableLinearIterableSerializer::class)
public interface KoneExtendableLinearIterable<Element> : KoneLinearIterable<Element>, KoneReversibleExtendableIterable<Element> {
    public override operator fun iterator(): KoneExtendableLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneRemovableLinearIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneRemovableLinearIterableSerializer::class)
public interface KoneRemovableLinearIterable<out Element> : KoneLinearIterable<Element>, KoneReversibleRemovableIterable<Element> {
    public override operator fun iterator(): KoneRemovableLinearIterator<Element>
}

/**
 * Represents [KoneIterable] that can be iterated by [KoneMutableLinearIterator].
 *
 * @param Element The type of elements in the iterable.
 */
@Serializable(with = DefaultKoneMutableLinearIterableSerializer::class)
public interface KoneMutableLinearIterable<Element> : KoneSettableLinearIterable<Element>, KoneExtendableLinearIterable<Element>, KoneRemovableLinearIterable<Element>, KoneReversibleMutableIterable<Element> {
    public override operator fun iterator(): KoneMutableLinearIterator<Element>
}