/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.delegates

import dev.lounres.kone.collections.KoneIterableCollection
import dev.lounres.kone.collections.KoneMutableSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.delegates.SetAction.*


public sealed interface SetAction<out E> {
    public data class Add<E>(val element: E): SetAction<E>
    public data class AddAll<E>(val elements: KoneIterableCollection<E>): SetAction<E>
    public data class Remove<E>(val element: E): SetAction<E>
    public data class RemoveAllThat<E>(val predicate: (E) -> Boolean): SetAction<E>
    public data object Clear: SetAction<Nothing>
}

public abstract class KoneMutableSetDelegate<E>(public val delegate: KoneMutableSet<E>) : KoneMutableSet<E> by delegate {
    public abstract fun beforeAction(state: KoneSet<E>, action: SetAction<E>)
    public abstract fun afterAction(state: KoneSet<E>, action: SetAction<E>)

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
    crossinline before: (state: KoneSet<E>, action: SetAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneSet<E>, action: SetAction<E>) -> Unit = { _, _ -> },
): KoneMutableSetDelegate<E> = object : KoneMutableSetDelegate<E>(initial) {
    override fun beforeAction(state: KoneSet<E>, action: SetAction<E>) = before(state, action)
    override fun afterAction(state: KoneSet<E>, action: SetAction<E>) = after(state, action)
}