/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


//public interface KoneContextualCollection<out E> {
//    public val size: UInt
//    context(Equality<E>)
//    public operator fun contains(element: @UnsafeVariance E): Boolean
//}
//
//public interface KoneExtendableContextualCollection<E> : KoneContextualCollection<E> {
//    context(Equality<E>)
//    public fun add(element: E)
//    context(Equality<E>)
//    public fun addAll(elements: KoneIterableContextualCollection<E>) {
//        for (e in elements) add(e)
//    }
//}
//
//public interface KoneRemovableContextualCollection<out E> : KoneContextualCollection<E> {
//    context(Equality<E>)
//    public fun remove(element: @UnsafeVariance E)
//    public fun removeAllThat(predicate: (element: E) -> Boolean)
//    public fun clear()
//}
//
//public interface KoneMutableContextualCollection<E> : KoneExtendableContextualCollection<E>, KoneRemovableContextualCollection<E>
//
//public interface KoneContextualList<out E> : KoneContextualCollection<E> {
//    context(Equality<E>)
//    override fun contains(element: @UnsafeVariance E): Boolean = indexThat { _, collectionElement -> element == collectionElement } != size
//
//    public operator fun get(index: UInt): E
//    public fun getMaybe(index: UInt): Option<E> =
//        if (index < size) Some(get(index))
//        else None
//    public fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
//        var i = 0u
//        while (i < size) {
//            if (predicate(i, get(i))) break
//            i++
//        }
//        return i
//    }
//    public fun lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
//        var i = size - 1u
//        while (i != UInt.MAX_VALUE) {
//            if (predicate(i, get(i))) break
//            i--
//        }
//        return i
//    }
//}
//
//public interface KoneSettableContextualList<E> : KoneContextualList<E> {
//    public operator fun set(index: UInt, element: E)
//}
//
//public interface KoneExtendableContextualList<E>: KoneContextualList<E>, KoneExtendableContextualCollection<E> {
//    public fun addAt(index: UInt, element: E)
//    public fun addAllAt(index: UInt, elements: KoneIterableContextualCollection<E>)
//}
//
//public interface KoneRemovableContextualList<out E>: KoneContextualList<E>, KoneRemovableContextualCollection<E> {
//    public fun removeAt(index: UInt)
//    public override fun removeAllThat(predicate: (element: E) -> Boolean) {
//        removeAllThatIndexed { _, element -> predicate(element) }
//    }
//    public fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean)
//}
//
//public interface KoneMutableContextualList<E> : KoneSettableContextualList<E>, KoneExtendableContextualList<E>, KoneRemovableContextualList<E>, KoneMutableContextualCollection<E> {
//    // FIXME: KT-65793
//    override fun contains(element: E): Boolean = indexThat { _, collectionElement -> element == collectionElement } != size
//}
//
//public interface KoneContextualSet<out E> : KoneContextualCollection<E>
//
//public interface KoneExtendableContextualSet<E> : KoneContextualSet<E>, KoneExtendableContextualCollection<E>
//
//public interface KoneRemovableContextualSet<out E> : KoneContextualSet<E>, KoneRemovableContextualCollection<E>
//
//public interface KoneMutableContextualSet<E> : KoneExtendableContextualSet<E>, KoneRemovableContextualSet<E>, KoneMutableContextualCollection<E>