/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.delegates

import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneMutableSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.delegates.KoneSetAction.*


public sealed interface KoneSetAction<out E> {
    public data class Add<E>(val element: E): KoneSetAction<E>
    public data class AddAll<E>(val elements: KoneIterableCollection<E>): KoneSetAction<E>
    public data class Remove<E>(val element: E): KoneSetAction<E>
    public data class RemoveAllThat<E>(val predicate: (E) -> Boolean): KoneSetAction<E>
    public data object Clear: KoneSetAction<Nothing>
}

public abstract class KoneMutableSetDelegate<E>(public val delegate: KoneMutableSet<E>) : KoneMutableSet<E> by delegate {
    public abstract fun beforeAction(state: KoneSet<E>, action: KoneSetAction<E>)
    public abstract fun afterAction(state: KoneSet<E>, action: KoneSetAction<E>)

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

    override fun remove(element: E) {
        beforeAction(delegate, Remove(element))
        delegate.remove(element)
        afterAction(delegate, Remove(element))
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
}

public inline fun <E> KoneMutableSetDelegate(
    initial: KoneMutableSet<E> = TODO(),
    crossinline before: (state: KoneSet<E>, action: KoneSetAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneSet<E>, action: KoneSetAction<E>) -> Unit = { _, _ -> },
): KoneMutableSetDelegate<E> = object : KoneMutableSetDelegate<E>(initial) {
    override fun beforeAction(state: KoneSet<E>, action: KoneSetAction<E>) = before(state, action)
    override fun afterAction(state: KoneSet<E>, action: KoneSetAction<E>) = after(state, action)
}