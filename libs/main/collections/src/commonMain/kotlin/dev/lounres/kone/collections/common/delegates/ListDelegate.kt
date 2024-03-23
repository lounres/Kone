/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.delegates

import dev.lounres.kone.collections.common.KoneIterableCollection
import dev.lounres.kone.collections.common.KoneList
import dev.lounres.kone.collections.common.KoneMutableList
import dev.lounres.kone.collections.common.delegates.KoneListAction.*
import dev.lounres.kone.collections.common.implementations.KoneGrowableArrayList


// TODO: Add more delegates as for sets

public sealed interface KoneListAction<out E> {
    public data class AddAt<out E>(val index: UInt, val element: E): KoneListAction<E>
    public data class AddAllAt<out E>(val index: UInt, val elements: KoneIterableCollection<E>): KoneListAction<E>
    public data class AddAtTheEnd<out E>(val element: E): KoneListAction<E>
    public data class AddAllAtTheEnd<out E>(val elements: KoneIterableCollection<E>): KoneListAction<E>
    public data class Remove<out E>(val element: E): KoneListAction<E>
    public data class RemoveAt<out E>(val index: UInt): KoneListAction<E>
    public data class RemoveAllThat<E>(val predicate: (E) -> Boolean): KoneListAction<E>
    public data object Clear: KoneListAction<Nothing>
}

public abstract class KoneMutableListDelegate<E>(public val delegate: KoneMutableList<E>) : KoneMutableList<E> by delegate {
    public abstract fun beforeAction(state: KoneList<E>, action: KoneListAction<E>)
    public abstract fun afterAction(state: KoneList<E>, action: KoneListAction<E>)

    override fun addAt(index: UInt, element: E) {
        beforeAction(delegate, AddAt(index, element))
        delegate.addAt(index, element)
        afterAction(delegate, AddAt(index, element))
    }

    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) {
        beforeAction(delegate, AddAllAt(index, elements))
        delegate.addAllAt(index, elements)
        afterAction(delegate, AddAllAt(index, elements))
    }

    override fun addAtTheEnd(element: E) {
        beforeAction(delegate, AddAtTheEnd(element))
        delegate.addAtTheEnd(element)
        afterAction(delegate, AddAtTheEnd(element))
    }

    override fun addAllAtTheEnd(elements: KoneIterableCollection<E>) {
        beforeAction(delegate, AddAllAtTheEnd(elements))
        delegate.addAllAtTheEnd(elements)
        afterAction(delegate, AddAllAtTheEnd(elements))
    }

    override fun remove(element: E) {
        beforeAction(delegate, Remove(element))
        delegate.remove(element)
        afterAction(delegate, Remove(element))
    }

    override fun removeAt(index: UInt) {
        beforeAction(delegate, RemoveAt(index))
        delegate.removeAt(index)
        afterAction(delegate, RemoveAt(index))
    }

    override fun removeAllThat(predicate: (E) -> Boolean) {
        beforeAction(delegate, RemoveAllThat(predicate))
        delegate.removeAllThat(predicate)
        afterAction(delegate, RemoveAllThat(predicate))
    }

    override fun clear() {
        beforeAction(delegate, Clear)
        delegate.clear()
        afterAction(delegate, Clear)
    }

    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneList<*>) return false
        if (this.size != other.size) return false

        if (other is KoneMutableListDelegate<*>) return this.delegate == other.delegate

        return this.delegate == other
    }
}

public inline fun <E> KoneMutableListDelegate(
    initial: KoneMutableList<E> = KoneGrowableArrayList(),
    crossinline before: (state: KoneList<E>, action: KoneListAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneList<E>, action: KoneListAction<E>) -> Unit = { _, _ -> },
): KoneMutableListDelegate<E> = object : KoneMutableListDelegate<E>(initial) {
    override fun beforeAction(state: KoneList<E>, action: KoneListAction<E>) = before(state, action)
    override fun afterAction(state: KoneList<E>, action: KoneListAction<E>) = after(state, action)
}