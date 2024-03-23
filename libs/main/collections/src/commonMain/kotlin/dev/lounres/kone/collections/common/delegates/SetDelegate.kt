/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.delegates

import dev.lounres.kone.collections.common.*
import dev.lounres.kone.collections.common.delegates.KoneSetAction.*
import dev.lounres.kone.comparison.Equality


public sealed interface KoneSetAction<out E> {
    public sealed interface Extending<out E>: KoneSetAction<E>
    public data class Add<out E>(val element: E): Extending<E>
    public data class AddAll<out E>(val elements: KoneIterableCollection<E>): Extending<E>

    public sealed interface Removing<out E>: KoneSetAction<E>
    public data class Remove<out E>(val element: E): Removing<E>
    public data class RemoveAllThat<E>(val predicate: (E) -> Boolean): Removing<E>
    public data object Clear: Removing<Nothing>
}

public abstract class KoneExtendableIterableSetDelegate<E>(public val delegate: KoneExtendableIterableSet<E>) : KoneExtendableIterableSet<E> by delegate {
    public abstract fun beforeAction(state: KoneIterableSet<E>, action: KoneSetAction.Extending<E>)
    public abstract fun afterAction(state: KoneIterableSet<E>, action: KoneSetAction.Extending<E>)

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

    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false

        if (other is KoneMutableListDelegate<*>) return this.delegate == other.delegate

        return this.delegate == other
    }
}

public inline fun <E> KoneExtendableIterableSetDelegate(
    initial: KoneExtendableIterableSet<E> = TODO(),
    crossinline before: (state: KoneIterableSet<E>, action: KoneSetAction.Extending<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneIterableSet<E>, action: KoneSetAction.Extending<E>) -> Unit = { _, _ -> },
): KoneExtendableIterableSetDelegate<E> = object : KoneExtendableIterableSetDelegate<E>(initial) {
    override fun beforeAction(state: KoneIterableSet<E>, action: KoneSetAction.Extending<E>) = before(state, action)
    override fun afterAction(state: KoneIterableSet<E>, action: KoneSetAction.Extending<E>) = after(state, action)
}

public abstract class KoneRemovableIterableSetDelegate<E>(public val delegate: KoneRemovableIterableSet<E>) : KoneRemovableIterableSet<E> by delegate {
    public abstract fun beforeAction(state: KoneIterableSet<E>, action: KoneSetAction.Removing<E>)
    public abstract fun afterAction(state: KoneIterableSet<E>, action: KoneSetAction.Removing<E>)

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

    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false

        if (other is KoneMutableListDelegate<*>) return this.delegate == other.delegate

        return this.delegate == other
    }
}

public inline fun <E> KoneRemovableIterableSetDelegate(
    initial: KoneRemovableIterableSet<E> = TODO(),
    crossinline before: (state: KoneIterableSet<E>, action: KoneSetAction.Removing<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneIterableSet<E>, action: KoneSetAction.Removing<E>) -> Unit = { _, _ -> },
): KoneRemovableIterableSetDelegate<E> = object : KoneRemovableIterableSetDelegate<E>(initial) {
    override fun beforeAction(state: KoneIterableSet<E>, action: KoneSetAction.Removing<E>) = before(state, action)
    override fun afterAction(state: KoneIterableSet<E>, action: KoneSetAction.Removing<E>) = after(state, action)
}

public abstract class KoneMutableIterableSetDelegate<E>(public val delegate: KoneMutableIterableSet<E>) : KoneMutableIterableSet<E> by delegate {
    public abstract fun beforeAction(state: KoneIterableSet<E>, action: KoneSetAction<E>)
    public abstract fun afterAction(state: KoneIterableSet<E>, action: KoneSetAction<E>)

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

    override fun hashCode(): Int = delegate.hashCode()
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is KoneSet<*>) return false
        if (this.size != other.size) return false

        if (other is KoneMutableListDelegate<*>) return this.delegate == other.delegate

        return this.delegate == other
    }
}

public inline fun <E> KoneMutableIterableSetDelegate(
    initial: KoneMutableIterableSet<E> = TODO(),
    crossinline before: (state: KoneIterableSet<E>, action: KoneSetAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneIterableSet<E>, action: KoneSetAction<E>) -> Unit = { _, _ -> },
): KoneMutableIterableSetDelegate<E> = object : KoneMutableIterableSetDelegate<E>(initial) {
    override fun beforeAction(state: KoneIterableSet<E>, action: KoneSetAction<E>) = before(state, action)
    override fun afterAction(state: KoneIterableSet<E>, action: KoneSetAction<E>) = after(state, action)
}