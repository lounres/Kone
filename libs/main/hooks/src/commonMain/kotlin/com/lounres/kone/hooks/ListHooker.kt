/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.hooks

import com.lounres.kone.collections.*
import com.lounres.kone.collections.delegates.*


public typealias ListHooker<E> = Hooker<KoneMutableList<E>, KoneList<E>, ListAction<E>>
public fun <E> ListHooker(list: KoneMutableList<E>): ListHooker<E> = ListHookerImpl(list)
internal class ListHookerImpl<E>(private val state: KoneMutableList<E>): Hooker<KoneMutableList<E>, KoneList<E>, ListAction<E>> {
    override val input: KoneMutableList<E> =
        KoneMutableListDelegate(
            initial = state,
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )
    override val output: KoneList<E> get() = state

    // TODO: Replace with my own mutable list?
    private val hooks: MutableList<Response<KoneList<E>, ListAction<E>>> = TODO()
    override fun hookUp(response: Response<KoneList<E>, ListAction<E>>) { hooks.add(response) }
}

public fun <E> KoneMutableList<E>.hooker(): ListHooker<E> = ListHooker(this)