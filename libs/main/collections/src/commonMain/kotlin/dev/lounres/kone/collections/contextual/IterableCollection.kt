/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.*


//public interface KoneIterableContextualCollection<out E> : KoneContextualCollection<E>, KoneIterable<E>
//
//public interface KoneReversibleIterableContextualCollection<out E> : KoneIterableContextualCollection<E>, KoneReversibleIterable<E>
//
//public interface KoneExtendableIterableContextualCollection<E> : KoneIterableContextualCollection<E>, KoneExtendableContextualCollection<E>
//
//public interface KoneReversibleExtendableIterableContextualCollection<E> : KoneExtendableIterableContextualCollection<E>, KoneReversibleIterableContextualCollection<E>
//
//public interface KoneRemovableIterableContextualCollection<out E> : KoneIterableContextualCollection<E>, KoneRemovableIterable<E>, KoneRemovableContextualCollection<E>
//
//public interface KoneReversibleRemovableIterableContextualCollection<out E> : KoneRemovableIterableContextualCollection<E>, KoneReversibleIterableContextualCollection<E>, KoneReversibleRemovableIterable<E>
//
//public interface KoneMutableIterableContextualCollection<E> : KoneExtendableIterableContextualCollection<E>, KoneRemovableIterableContextualCollection<E>
//
//public interface KoneReversibleMutableIterableContextualCollection<E> : KoneReversibleExtendableIterableContextualCollection<E>, KoneReversibleRemovableIterableContextualCollection<E>, KoneMutableIterableContextualCollection<E>
//
//public interface KoneIterableContextualList<out E> : KoneContextualList<E>, KoneIterableContextualCollection<E>, KoneLinearIterable<E> {
//    public fun iteratorFrom(index: UInt): KoneLinearIterator<E> {
//        require(index <= size)
//        val iterator = iterator()
//        for (i in 0u until index) iterator.moveNext()
//        return iterator
//    }
//    public override fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
//        var i = 0u
//        val iterator = iterator()
//        while (iterator.hasNext()) {
//            if (predicate(i, iterator.getAndMoveNext())) break
//            i++
//        }
//        return i
//    }
//    public override fun lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt {
//        var i = size - 1u
//        val iterator = iteratorFrom(size)
//        while (iterator.hasPrevious()) {
//            if (predicate(i, iterator.getAndMovePrevious())) break
//            i--
//        }
//        return i
//    }
//}
//
//public interface KoneSettableIterableContextualList<E> : KoneIterableContextualList<E>, KoneSettableContextualList<E>, KoneSettableLinearIterable<E> {
//    public override fun iteratorFrom(index: UInt): KoneSettableLinearIterator<E> {
//        require(index <= size)
//        val iterator = iterator()
//        for (i in 0u until index) iterator.moveNext()
//        return iterator
//    }
//}
//
//public interface KoneMutableIterableContextualList<E> : KoneMutableContextualList<E>, KoneSettableIterableContextualList<E>, KoneReversibleMutableIterableContextualCollection<E>, KoneMutableLinearIterable<E> {
//    // FIXME: KT-65793
//    override fun contains(element: E): Boolean = super<KoneMutableContextualList>.contains(element)
//    public override fun iteratorFrom(index: UInt): KoneMutableLinearIterator<E> {
//        require(index <= size)
//        val iterator = iterator()
//        for (i in 0u until index) iterator.moveNext()
//        return iterator
//    }
//}
//
//public interface KoneIterableContextualSet<out E> : KoneContextualSet<E>, KoneIterableContextualCollection<E>
//
//public interface KoneExtendableIterableContextualSet<E> : KoneExtendableContextualSet<E>, KoneIterableContextualSet<E>, KoneExtendableIterableContextualCollection<E>
//
//public interface KoneRemovableIterableContextualSet<out E> : KoneRemovableContextualSet<E>, KoneIterableContextualSet<E>, KoneRemovableIterableContextualCollection<E>
//
//public interface KoneMutableIterableContextualSet<E> : KoneMutableContextualSet<E>, KoneRemovableIterableContextualSet<E>, KoneExtendableIterableContextualSet<E>, KoneMutableIterableContextualCollection<E>