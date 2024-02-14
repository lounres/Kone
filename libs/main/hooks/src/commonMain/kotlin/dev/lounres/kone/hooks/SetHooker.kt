/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hooks

import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.standard.*
import dev.lounres.kone.collections.standard.delegates.KoneExtendableIterableSetDelegate
import dev.lounres.kone.collections.standard.delegates.KoneMutableIterableSetDelegate
import dev.lounres.kone.collections.standard.delegates.KoneRemovableIterableSetDelegate
import dev.lounres.kone.collections.standard.delegates.KoneSetAction


public typealias ExtendableSetHookable<E> = Hookable<KoneIterableSet<E>, KoneSetAction.Extending<E>>
public typealias RemovableSetHookable<E> = Hookable<KoneIterableSet<E>, KoneSetAction.Removing<E>>
public typealias MutableSetHookable<E> = Hookable<KoneIterableSet<E>, KoneSetAction<E>>

public typealias ExtendableSetHooker<E> = Hooker<KoneExtendableSet<E>, KoneIterableSet<E>, KoneSetAction.Extending<E>>
public typealias RemovableSetHooker<E> = Hooker<KoneRemovableSet<E>, KoneIterableSet<E>, KoneSetAction.Removing<E>>
public typealias MutableSetHooker<E> = Hooker<KoneMutableSet<E>, KoneIterableSet<E>, KoneSetAction<E>>

public fun <E> ExtendableSetHooker(set: KoneExtendableIterableSet<E>): ExtendableSetHooker<E> = ExtendableSetHookerImpl(set)
internal class ExtendableSetHookerImpl<E>(private val state: KoneExtendableIterableSet<E>): ExtendableSetHooker<E>, AbstractHookable<KoneIterableSet<E>, KoneSetAction.Extending<E>>() {
    override val input: KoneExtendableSet<E> =
        KoneExtendableIterableSetDelegate(
            initial = state,
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )
    override val output: KoneIterableSet<E> get() = state
}
public fun <E> RemovableSetHooker(set: KoneRemovableIterableSet<E>): RemovableSetHooker<E> = RemovableSetHookerImpl(set)
internal class RemovableSetHookerImpl<E>(private val state: KoneRemovableIterableSet<E>): RemovableSetHooker<E>, AbstractHookable<KoneIterableSet<E>, KoneSetAction.Removing<E>>() {
    override val input: KoneRemovableSet<E> =
        KoneRemovableIterableSetDelegate(
            initial = state,
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )
    override val output: KoneIterableSet<E> get() = state
}
public fun <E> MutableSetHooker(set: KoneMutableIterableSet<E>): MutableSetHooker<E> = MutableSetHookerImpl(set)
internal class MutableSetHookerImpl<E>(private val state: KoneMutableIterableSet<E>): Hooker<KoneMutableSet<E>, KoneIterableSet<E>, KoneSetAction<E>>, AbstractHookable<KoneIterableSet<E>, KoneSetAction<E>>() {
    override val input: KoneMutableSet<E> =
        KoneMutableIterableSetDelegate(
            initial = state,
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )
    override val output: KoneIterableSet<E> get() = state
}

public fun <E> KoneMutableIterableSet<E>.hooker(): MutableSetHooker<E> = MutableSetHooker(this)