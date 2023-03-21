/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.hooks

import com.lounres.kone.collections.*
import com.lounres.kone.collections.actions.*


public typealias ListHooker<E> = Hooker<KoneMutableList<E>, KoneList<E>, ListAction<E>>
public fun <E> ListHooker(list: KoneList<E>): ListHooker<E> = ListHookerImpl(list)
internal class ListHookerImpl<E>(list: KoneList<E>): Hooker<KoneMutableList<E>, KoneList<E>, ListAction<E>> {
    override val input: KoneMutableList<E> =
        KoneMutableListDelegate(
            initial = /*list*/ TODO(),
            before = { state, action -> for (hook in hooks) hook.respondBeforeAction(state, action) },
            after = { state, action -> for (hook in hooks) hook.respondAfterAction(state, action) },
        )

    // TODO: Replace with my own mutable list?
    private val hooks: MutableList<Response<KoneList<E>, ListAction<E>>> = TODO()
    override fun hookUp(response: Response<KoneList<E>, ListAction<E>>) { hooks.add(response) }
}

public fun <E> KoneList<E>.hooker(): ListHooker<E> = ListHooker(this)