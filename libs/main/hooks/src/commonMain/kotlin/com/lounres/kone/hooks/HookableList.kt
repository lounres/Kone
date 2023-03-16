/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.hooks

import com.lounres.kone.collections.*


public sealed interface SetAction<out E> {
    public data class Add<E>(val element: E): SetAction<E>
    public data class AddAll<E>(val elements: KoneIterableCollection<E>): SetAction<E>
    public data class Remove<E>(val element: E): SetAction<E>
    public data class RemoveAll<E>(val elements: KoneIterableCollection<E>): SetAction<E>
    public data class Retain<E>(val element: E): SetAction<E>
    public data class RetainAll<E>(val elements: KoneIterableCollection<E>): SetAction<E>
    public data object Clear: SetAction<Nothing>
}

public class SetHooker<E>(public val set: KoneMutableSet<E>): Hooker<KoneMutableSet<E>, KoneSet<E>, SetAction<E>> {
    override val input: KoneMutableSet<E> = KoneMutableSetDelegate(set)

    // TODO: Replace with my own mutable list?
    private val hooks: MutableList<Response<KoneSet<E>, SetAction<E>>> = TODO()
    override fun hookUp(response: Response<KoneSet<E>, SetAction<E>>) {
        hooks.add(response)
    }

    private inner class KoneMutableSetDelegate(private val delegate: KoneMutableSet<E>) : KoneMutableSet<E> by delegate {
        override fun add(element: E) {
            hooks.map { it.respondBeforeAction(delegate, SetAction.Add(element)) }
            delegate.add(element)
            hooks.map { it.respondAfterAction(delegate, SetAction.Add(element)) }
        }
        override fun addAll(elements: KoneIterableCollection<E>) {
            hooks.map { it.respondBeforeAction(delegate, SetAction.AddAll(elements)) }
            delegate.addAll(elements)
            hooks.map { it.respondAfterAction(delegate, SetAction.AddAll(elements)) }
        }
        override fun remove(element: E) {
            hooks.map { it.respondBeforeAction(delegate, SetAction.Remove(element)) }
            delegate.remove(element)
            hooks.map { it.respondAfterAction(delegate, SetAction.Remove(element)) }
        }
        override fun removeAll(elements: KoneIterableCollection<E>) {
            hooks.map { it.respondBeforeAction(delegate, SetAction.RemoveAll(elements)) }
            delegate.removeAll(elements)
            hooks.map { it.respondAfterAction(delegate, SetAction.RemoveAll(elements)) }
        }
        override fun retain(element: E) {
            hooks.map { it.respondBeforeAction(delegate, SetAction.Retain(element)) }
            delegate.retain(element)
            hooks.map { it.respondAfterAction(delegate, SetAction.Retain(element)) }
        }
        override fun retainAll(elements: KoneIterableCollection<E>) {
            hooks.map { it.respondBeforeAction(delegate, SetAction.RetainAll(elements)) }
            delegate.retainAll(elements)
            hooks.map { it.respondAfterAction(delegate, SetAction.RetainAll(elements)) }
        }
        override fun clear() {
            hooks.map { it.respondBeforeAction(delegate, SetAction.Clear) }
            delegate.clear()
            hooks.map { it.respondAfterAction(delegate, SetAction.Clear) }
        }
    }
}