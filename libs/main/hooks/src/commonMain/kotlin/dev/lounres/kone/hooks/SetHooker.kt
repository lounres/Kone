/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hooks

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.KoneMutableSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.delegates.KoneMutableSetDelegate
import dev.lounres.kone.collections.delegates.ListAction
import dev.lounres.kone.collections.delegates.SetAction


public typealias SetHookable<E> = Hookable<KoneList<E>, ListAction<E>>
public typealias SetHooker<E> = Hooker<KoneMutableSet<E>, KoneSet<E>, SetAction<E>>
public fun <E> SetHooker(set: KoneMutableSet<E>): SetHooker<E> = SetHookerImpl(set)
internal class SetHookerImpl<E>(private val state: KoneMutableSet<E>):
    Hooker<KoneMutableSet<E>, KoneSet<E>, SetAction<E>> {
    override val input: KoneMutableSet<E> =
        KoneMutableSetDelegate(
            initial = state,
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )
    override val output: KoneSet<E> get() = state

    // TODO: Replace with my own mutable list?
    private val hooks: MutableList<Response<KoneSet<E>, SetAction<E>>> = TODO()
    override fun hookUp(response: Response<KoneSet<E>, SetAction<E>>) { hooks.add(response) }
}

public fun <E> KoneMutableSet<E>.hooker(): SetHooker<E> = SetHooker(this)