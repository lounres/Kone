/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hooks

import dev.lounres.kone.collections.forEach
import kotlin.reflect.KProperty


public data class UpdateAction<out O>(val output: O)

public interface UpdatableState<in O> {
    public fun set(value: O)
}

public typealias UpdateHookable<O> = Hookable<O, UpdateAction<O>>
public typealias UpdateHooker<O> = Hooker<UpdatableState<O>, O, UpdateAction<O>>
public fun <O> UpdateHooker(state: O): UpdateHooker<O> = UpdateHookerImpl(state)
internal class UpdateHookerImpl<O>(private var state: O): UpdatableState<O>, UpdateHooker<O>, AbstractHookable<O, UpdateAction<O>>() {
    override val input: UpdatableState<O> get() = this
    override val output: O get() = state
    override fun set(value: O) {
        hooks.forEach { it.respondBeforeAction(state, UpdateAction(value)) }
        state = value
        hooks.forEach { it.respondAfterAction(state, UpdateAction(value)) }
    }
}

public operator fun <O> UpdateHooker<O>.setValue(thisRef: Any?, property: KProperty<*>, value: O) { input.set(value) }

public fun <O, R> Hookable<O, *>.updateHookableBy(block: (O) -> R): UpdateHookable<R> =
    UpdateHooker(block(output)).also {
        hookUp(ResponseAfterAction { entity, _ -> it.input.set(block(entity)) })
    }