/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.hooks

import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList
import kotlin.reflect.KProperty


public interface Hookable<out O, out A> {
    public val output: O
    public fun hookUp(response: Response<O, A>)
}

/**
 * @param A type of the possible actions.
 * @param I type of the [input] entity that is mutated by the actions.
 */
public interface Hooker<out I, out O, out A>: Hookable<O, A> {
    public val input: I
}

public interface Response<in O, in A> {
    public fun respondBeforeAction(entity: O, action: A)
    public fun respondAfterAction(entity: O, action: A)
}

public inline fun <O, A> Response(
    crossinline respondBefore: (entity: O, action: A) -> Unit,
    crossinline respondAfter: (entity: O, action: A) -> Unit,
): Response<O, A> =
    object : Response<O, A> {
        override fun respondBeforeAction(entity: O, action: A) = respondBefore(entity, action)
        override fun respondAfterAction(entity: O, action: A) = respondAfter(entity, action)
    }

@Suppress("FunctionName")
public inline fun <O, A> ResponseBeforeAction(crossinline respond: (entity: O, action: A) -> Unit): Response<O, A> =
    object : Response<O, A> {
        override fun respondBeforeAction(entity: O, action: A) = respond(entity, action)
        override fun respondAfterAction(entity: O, action: A) {}
    }

@Suppress("FunctionName")
public inline fun <O, A> ResponseAfterAction(crossinline respond: (entity: O, action: A) -> Unit): Response<O, A> =
    object : Response<O, A> {
        override fun respondBeforeAction(entity: O, action: A) {}
        override fun respondAfterAction(entity: O, action: A) = respond(entity, action)
    }

// TODO: Check of `O` and `A` can really be covariant
public abstract class AbstractHookable<out O, out A>(
    protected val hooks: KoneMutableIterableList<Response<@UnsafeVariance O, @UnsafeVariance A>> = KoneGrowableArrayList()
): Hookable<O, A> {
    override fun hookUp(response: Response<O, A>) { hooks.add(response) }
}

public operator fun <O> Hookable<O, *>.getValue(thisRef: Any?, property: KProperty<*>): O = output