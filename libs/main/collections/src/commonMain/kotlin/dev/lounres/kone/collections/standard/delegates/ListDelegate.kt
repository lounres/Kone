/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.delegates

import dev.lounres.kone.collections.standard.KoneIterableCollection
import dev.lounres.kone.collections.standard.KoneList
import dev.lounres.kone.collections.standard.KoneMutableList
import dev.lounres.kone.collections.standard.delegates.KoneListAction.*
import dev.lounres.kone.collections.standard.implementations.KoneGrowableArrayList


// TODO: Add more delegates as for sets

public sealed interface KoneListAction<out E> {
    public data class Add<E>(val element: E): KoneListAction<E>
    public data class AddAll<E>(val elements: KoneIterableCollection<E>): KoneListAction<E>
    public data class AddAt<E>(val index: UInt, val element: E): KoneListAction<E>
    public data class AddAllIndex<E>(val index: UInt, val elements: KoneIterableCollection<E>): KoneListAction<E>
    public data class Remove<E>(val element: E): KoneListAction<E>
    public data class RemoveAt<E>(val index: UInt): KoneListAction<E>
    public data class RemoveAllThat<E>(val predicate: (E) -> Boolean): KoneListAction<E>
    public data object Clear: KoneListAction<Nothing>
}

public abstract class KoneMutableListDelegate<E>(public val delegate: KoneMutableList<E>) : KoneMutableList<E> by delegate {
    public abstract fun beforeAction(state: KoneList<E>, action: KoneListAction<E>)
    public abstract fun afterAction(state: KoneList<E>, action: KoneListAction<E>)

    override fun add(element: E) {
        beforeAction(delegate, Add(element))
        delegate.add(element)
        afterAction(delegate, Add(element))
    }

    override fun addAll(elements: KoneIterableCollection<E>) {
        beforeAction(delegate, AddAll(elements))
        delegate.addAll(elements)
        afterAction(delegate, AddAll(elements))
    }

    override fun addAt(index: UInt, element: E) {
        beforeAction(delegate, AddAt(index, element))
        delegate.addAt(index, element)
        afterAction(delegate, AddAt(index, element))
    }

    override fun addAllAt(index: UInt, elements: KoneIterableCollection<E>) {
        beforeAction(delegate, AddAllIndex(index, elements))
        delegate.addAllAt(index, elements)
        afterAction(delegate, AddAllIndex(index, elements))
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