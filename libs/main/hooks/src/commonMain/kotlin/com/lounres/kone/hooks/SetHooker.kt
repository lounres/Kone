/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.hooks

import com.lounres.kone.collections.*
import com.lounres.kone.collections.actions.*


public typealias SetHooker<E> = Hooker<KoneMutableSet<E>, KoneSet<E>, SetAction<E>>
public fun <E> SetHooker(set: KoneSet<E>): SetHooker<E> = SetHookerImpl(set)
public class SetHookerImpl<E>(set: KoneSet<E>): Hooker<KoneMutableSet<E>, KoneSet<E>, SetAction<E>> {
    override val input: KoneMutableSet<E> =
        KoneMutableSetDelegate(
            initial = /*set*/ TODO(),
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )

    // TODO: Replace with my own mutable list?
    private val hooks: MutableList<Response<KoneSet<E>, SetAction<E>>> = TODO()
    override fun hookUp(response: Response<KoneSet<E>, SetAction<E>>) { hooks.add(response) }
}

public fun <E> KoneSet<E>.hooker(): SetHooker<E> = SetHooker(this)