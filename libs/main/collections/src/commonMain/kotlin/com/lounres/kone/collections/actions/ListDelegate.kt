/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.collections.actions

import com.lounres.kone.collections.KoneIterableCollection
import com.lounres.kone.collections.KoneList
import com.lounres.kone.collections.KoneMutableList
import com.lounres.kone.collections.actions.ListAction.*


public sealed interface ListAction<out E> {
    public data class Add<E>(val element: E): ListAction<E>
    public data class AddAll<E>(val elements: KoneIterableCollection<E>): ListAction<E>
    public data class AddIndex<E>(val index: UInt, val element: E): ListAction<E>
    public data class AddAllIndex<E>(val index: UInt, val elements: KoneIterableCollection<E>): ListAction<E>
    public data class Remove<E>(val element: E): ListAction<E>
    public data class RemoveAt<E>(val index: UInt): ListAction<E>
    public data class RemoveAll<E>(val elements: KoneIterableCollection<E>): ListAction<E>
    public data class RetainAll<E>(val elements: KoneIterableCollection<E>): ListAction<E>
    public data object Clear: ListAction<Nothing>
}

public abstract class KoneMutableListDelegate<E>(public val delegate: KoneMutableList<E>) : KoneMutableList<E> by delegate {
    public abstract fun beforeAction(state: KoneList<E>, action: ListAction<E>)
    public abstract fun afterAction(state: KoneList<E>, action: ListAction<E>)

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

    override fun add(index: UInt, element: E) {
        beforeAction(delegate, AddIndex(index, element))
        delegate.add(index, element)
        afterAction(delegate, AddIndex(index, element))
    }

    override fun addAll(index: UInt, elements: KoneIterableCollection<E>) {
        beforeAction(delegate, AddAllIndex(index, elements))
        delegate.addAll(index, elements)
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

    override fun removeAll(elements: KoneIterableCollection<E>) {
        beforeAction(delegate, RemoveAll(elements))
        delegate.removeAll(elements)
        afterAction(delegate, RemoveAll(elements))
    }

    override fun retainAll(elements: KoneIterableCollection<E>) {
        beforeAction(delegate, RetainAll(elements))
        delegate.retainAll(elements)
        afterAction(delegate, RetainAll(elements))
    }

    override fun clear() {
        beforeAction(delegate, Clear)
        delegate.clear()
        afterAction(delegate, Clear)
    }
}

public inline fun <E> KoneMutableListDelegate(
    initial: KoneMutableList<E> = TODO(),
    crossinline before: (state: KoneList<E>, action: ListAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneList<E>, action: ListAction<E>) -> Unit = { _, _ -> },
): KoneMutableListDelegate<E> = object : KoneMutableListDelegate<E>(initial) {
    override fun beforeAction(state: KoneList<E>, action: ListAction<E>) = before(state, action)
    override fun afterAction(state: KoneList<E>, action: ListAction<E>) = after(state, action)
}