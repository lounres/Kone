/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package com.lounres.kone.hooks


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

@Suppress("FunctionName")
public fun <O, A> ResponseBeforeAction(respond: (entity: O, action: A) -> Unit ): Response<O, A> =
    object : Response<O, A> {
        override fun respondBeforeAction(entity: O, action: A) = respond(entity, action)
        override fun respondAfterAction(entity: O, action: A) {}
    }

@Suppress("FunctionName")
public fun <O, A> ResponseAfterAction(respond: (entity: O, action: A) -> Unit ): Response<O, A> =
    object : Response<O, A> {
        override fun respondBeforeAction(entity: O, action: A) {}
        override fun respondAfterAction(entity: O, action: A) = respond(entity, action)
    }