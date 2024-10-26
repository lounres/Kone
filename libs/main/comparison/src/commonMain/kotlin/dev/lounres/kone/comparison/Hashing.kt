/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison

import dev.lounres.kone.context.invoke


/**
 * Describes a context that provides [hash] operator besides inherited [equalsTo] operator. This operator should return
 * the same value for elements that are equal according to [equalsTo] operator.
 *
 * Such contexts are used instead of usual [hashCode] overloading for several reasons. Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Hashing<in E> : Equality<E> {
    public fun E.hash(): Int = this.hashCode()
}

/**
 * [Hashing] builder from a [equalizer] that checks equality of the `left` and `right` elements and [hasher]
 * that computes hash of provided element.
 */
public inline fun <E> Hashing(crossinline equalizer: (left: E, right: E) -> Boolean, crossinline hasher: (E) -> Int): Hashing<E> =
    object : Hashing<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer(this, other)
        override fun E.hash(): Int = hasher(this)
    }

/**
 * [Hashing] builder from a [equalizer] that checks equality of the `left` and `right` elements and [hasher]
 * that computes hash of provided element.
 */
public inline fun <E> Hashing(equalizer: Equality<E>, crossinline hasher: (E) -> Int): Hashing<E> =
    object : Hashing<E> {
        override fun E.equalsTo(other: E): Boolean = equalizer { this eq other }
        override fun E.hash(): Int = hasher(this)
    }

/**
 * Returns [Hashing] instance which [Equality.equalsTo] operator just uses [Any.equals] operator's result as a return value
 * and which [Hashing.hash] operator just uses [Any.hashCode] operator's result as a return value.
 */
public fun <E> defaultHashing(): Hashing<E> = DefaultContext
/**
 * Returns [Hashing] instance which [Equality.equalsTo] operator just uses absolute equality `===` operator's result as a return value
 * and which [Hashing.hash] operator just uses [Any.hashCode] operator's result as a return value.
 */
public fun <E> absoluteHashing(): Hashing<E> = AbsoluteContext