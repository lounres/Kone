/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.concurrentCollections

import dev.lounres.kone.collections.KoneCollection
import dev.lounres.kone.collections.KoneExtendableCollection
import dev.lounres.kone.collections.KoneExtendableList
import dev.lounres.kone.collections.KoneExtendableSet
import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableCollection
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.KoneMutableSet
import dev.lounres.kone.collections.KoneRemovableCollection
import dev.lounres.kone.collections.KoneRemovableList
import dev.lounres.kone.collections.KoneRemovableSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.KoneSettableList
import dev.lounres.kone.option.Option
import kotlinx.atomicfu.locks.SynchronizedObject
import kotlinx.atomicfu.locks.synchronized


public interface KoneLockingCollectionWrapper<out E>: KoneLockingWrapper, KoneCollection<E> {
    override val backingImplementation: KoneCollection<E>
    override val size: UInt get() = synchronized { backingImplementation.size }
    override fun contains(element: @UnsafeVariance E): Boolean = synchronized { backingImplementation.contains(element) }
}

public fun <E> KoneLockingCollectionWrapper(backingImplementation: KoneCollection<E>): KoneLockingCollectionWrapper<E> =
    object : SynchronizedObject(), KoneLockingCollectionWrapper<E> {
        override val backingImplementation: KoneCollection<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingExtendableCollectionWrapper<E>: KoneLockingCollectionWrapper<E>, KoneExtendableCollection<E> {
    override val backingImplementation: KoneExtendableCollection<E>
    override fun add(element: E) {
        synchronized { backingImplementation.add(element) }
    }
    override fun addSeveral(number: UInt, builder: (UInt) -> E) {
        synchronized { backingImplementation.addSeveral(number, builder) }
    }
    override fun addAllFrom(elements: KoneIterableCollection<E>) {
        synchronized { backingImplementation.addAllFrom(elements) }
    }
}

public fun <E> KoneLockingExtendableCollectionWrapper(backingImplementation: KoneExtendableCollection<E>): KoneLockingExtendableCollectionWrapper<E> =
    object : SynchronizedObject(), KoneLockingExtendableCollectionWrapper<E> {
        override val backingImplementation: KoneExtendableCollection<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingRemovableCollectionWrapper<out E>: KoneLockingCollectionWrapper<E>, KoneRemovableCollection<E> {
    override val backingImplementation: KoneRemovableCollection<E>
    override fun remove(element: @UnsafeVariance E) {
        synchronized { backingImplementation.remove(element) }
    }
    override fun removeAllThat(predicate: (element: E) -> Boolean) {
        synchronized { backingImplementation.removeAllThat(predicate) }
    }
    override fun removeAll() {
        synchronized { backingImplementation.removeAll() }
    }
}

public fun <E> KoneLockingRemovableCollectionWrapper(backingImplementation: KoneRemovableCollection<E>): KoneLockingRemovableCollectionWrapper<E> =
    object : SynchronizedObject(), KoneLockingRemovableCollectionWrapper<E> {
        override val backingImplementation: KoneRemovableCollection<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingMutableCollectionWrapper<E>: KoneLockingExtendableCollectionWrapper<E>, KoneLockingRemovableCollectionWrapper<E>, KoneMutableCollection<E> {
    override val backingImplementation: KoneMutableCollection<E>
}

public fun <E> KoneLockingMutableCollectionWrapper(backingImplementation: KoneMutableCollection<E>): KoneLockingMutableCollectionWrapper<E> =
    object : SynchronizedObject(), KoneLockingMutableCollectionWrapper<E> {
        override val backingImplementation: KoneMutableCollection<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingListWrapper<out E>: KoneLockingCollectionWrapper<E>, KoneList<E> {
    override val backingImplementation: KoneList<E>
    override fun get(index: UInt): E = synchronized { backingImplementation.get(index) }
    override fun getMaybe(index: UInt): Option<E> = synchronized { backingImplementation.getMaybe(index) }
    override fun indexOf(element: @UnsafeVariance E): UInt = synchronized { backingImplementation.indexOf(element) }
    override fun indexThat(predicate: (index: UInt, element: E) -> Boolean): UInt = synchronized { backingImplementation.indexThat(predicate) }
    override fun lastIndexOf(element: @UnsafeVariance E): UInt = synchronized { backingImplementation.lastIndexOf(element) }
    override fun lastIndexThat(predicate: (index: UInt, element: E) -> Boolean): UInt = synchronized { backingImplementation.lastIndexThat(predicate) }
}

public fun <E> KoneLockingListWrapper(backingImplementation: KoneList<E>): KoneLockingListWrapper<E> =
    object : SynchronizedObject(), KoneLockingListWrapper<E> {
        override val backingImplementation: KoneList<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingSettableListWrapper<E>: KoneLockingListWrapper<E>, KoneSettableList<E> {
    override val backingImplementation: KoneSettableList<E>
    override fun set(index: UInt, element: E) {
        synchronized { backingImplementation.set(index, element) }
    }
}

public fun <E> KoneLockingSettableListWrapper(backingImplementation: KoneSettableList<E>): KoneLockingSettableListWrapper<E> =
    object : SynchronizedObject(), KoneLockingSettableListWrapper<E> {
        override val backingImplementation: KoneSettableList<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingExtendableListWrapper<E>: KoneLockingListWrapper<E>, KoneLockingExtendableCollectionWrapper<E>, KoneExtendableList<E> {
    override val backingImplementation: KoneExtendableList<E>
    override fun addAt(index: UInt, element: E) {
        synchronized { backingImplementation.addAt(index, element) }
    }
    override fun addSeveralAt(number: UInt, index: UInt, builder: (UInt) -> E) {
        synchronized { backingImplementation.addSeveralAt(number, index, builder) }
    }
    override fun addAllFromAt(index: UInt, elements: KoneIterableCollection<E>) {
        synchronized { backingImplementation.addAllFromAt(index, elements) }
    }
}

public fun <E> KoneLockingExtendableListWrapper(backingImplementation: KoneExtendableList<E>): KoneLockingExtendableListWrapper<E> =
    object : SynchronizedObject(), KoneLockingExtendableListWrapper<E> {
        override val backingImplementation: KoneExtendableList<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingRemovableListWrapper<E>: KoneLockingListWrapper<E>, KoneLockingRemovableCollectionWrapper<E>, KoneRemovableList<E> {
    override val backingImplementation: KoneRemovableList<E>
    override fun removeAt(index: UInt) {
        synchronized { backingImplementation.removeAt(index) }
    }
    override fun removeAllThatIndexed(predicate: (index: UInt, element: E) -> Boolean) {
        synchronized { backingImplementation.removeAllThatIndexed(predicate) }
    }
}

public fun <E> KoneLockingRemovableListWrapper(backingImplementation: KoneRemovableList<E>): KoneLockingRemovableListWrapper<E> =
    object : SynchronizedObject(), KoneLockingRemovableListWrapper<E> {
        override val backingImplementation: KoneRemovableList<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingMutableListWrapper<E>: KoneLockingSettableListWrapper<E>, KoneLockingExtendableListWrapper<E>, KoneLockingRemovableListWrapper<E>, KoneLockingMutableCollectionWrapper<E>, KoneMutableList<E> {
    override val backingImplementation: KoneMutableList<E>
}

public fun <E> KoneLockingMutableListWrapper(backingImplementation: KoneMutableList<E>): KoneLockingMutableListWrapper<E> =
    object : SynchronizedObject(), KoneLockingMutableListWrapper<E> {
        override val backingImplementation: KoneMutableList<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }


public interface KoneLockingSetWrapper<out E> : KoneLockingCollectionWrapper<E>, KoneSet<E> {
    override val backingImplementation: KoneSet<E>
}

public fun <E> KoneLockingSetWrapper(backingImplementation: KoneSet<E>): KoneLockingSetWrapper<E> =
    object : SynchronizedObject(), KoneLockingSetWrapper<E> {
        override val backingImplementation: KoneSet<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingExtendableSetWrapper<E> : KoneLockingSetWrapper<E>, KoneLockingExtendableCollectionWrapper<E>, KoneExtendableSet<E> {
    override val backingImplementation: KoneExtendableSet<E>
}

public fun <E> KoneLockingExtendableSetWrapper(backingImplementation: KoneExtendableSet<E>): KoneLockingExtendableSetWrapper<E> =
    object : SynchronizedObject(), KoneLockingExtendableSetWrapper<E> {
        override val backingImplementation: KoneExtendableSet<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingRemovableSetWrapper<out E> : KoneLockingSetWrapper<E>, KoneLockingRemovableCollectionWrapper<E>, KoneRemovableSet<E> {
    override val backingImplementation: KoneRemovableSet<E>
}

public fun <E> KoneLockingRemovableSetWrapper(backingImplementation: KoneRemovableSet<E>): KoneLockingRemovableSetWrapper<E> =
    object : SynchronizedObject(), KoneLockingRemovableSetWrapper<E> {
        override val backingImplementation: KoneRemovableSet<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }

public interface KoneLockingMutableSetWrapper<E> : KoneLockingExtendableSetWrapper<E>, KoneLockingRemovableSetWrapper<E>, KoneLockingMutableCollectionWrapper<E>, KoneMutableSet<E> {
    override val backingImplementation: KoneMutableSet<E>
}

public fun <E> KoneLockingMutableSetWrapper(backingImplementation: KoneMutableSet<E>): KoneLockingMutableSetWrapper<E> =
    object : SynchronizedObject(), KoneLockingMutableSetWrapper<E> {
        override val backingImplementation: KoneMutableSet<E> = backingImplementation
        override fun <R> synchronized(block: () -> R): R = synchronized(this, block)
    }