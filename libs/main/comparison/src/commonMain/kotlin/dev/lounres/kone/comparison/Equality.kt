/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.comparison

import dev.lounres.kone.context.KoneContext


/**
 * Describes a context that provides equality [equivalence relation](https://en.wikipedia.org/wiki/Equivalence_relation)
 * on the set of elements of type [E]. The relation is described by [equalsTo] function.
 *
 * Such contexts are used instead of usual [equals] overloading for several reasons. Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Equality<in E>: KoneContext {
    /**
     * Checks equality of [this] and [other] elements.
     */
    public infix fun E.equalsTo(other: E): Boolean = this == other
}

/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [Equality.equalsTo].
 */
// FIXME: KT-5351
context(Equality<E>)
public inline infix fun <E> E.notEqualsTo(other: E): Boolean = !(this equalsTo other)
/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for [Equality.equalsTo].
 */
context(Equality<E>)
public inline infix fun <E> E.eq(other: E): Boolean = this equalsTo other
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [Equality.equalsTo].
 */
// FIXME: KT-5351
context(Equality<E>)
public inline infix fun <E> E.neq(other: E): Boolean = !(this equalsTo other)

/**
 * [Equality] builder from a [equalizer] that checks equality of the `left` and `right` elements.
 */
public inline fun <E> Equality(crossinline equalizer: (left: E, right: E) -> Boolean): Equality<E> =
    object : Equality<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer(this, other)
    }

/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses [Any.equals] operator's result as a return value.
 */
public fun <E> defaultEquality(): Equality<E> = DefaultContext
/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses absolute equality `===` operator's result as a return value.
 */
public fun <E> absoluteEquality(): Equality<E> = AbsoluteContext