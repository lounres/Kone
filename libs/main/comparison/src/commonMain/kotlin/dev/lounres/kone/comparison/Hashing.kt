/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison


public interface Hashing<in E> : Equality<E> {
    public fun E.hash(): Int = this.hashCode()
}

public inline fun <E> Hashing(crossinline comparator: (left: E, right: E) -> Boolean, crossinline hasher: (E) -> Int): Hashing<E> =
    object : Hashing<E> {
        override fun E.equalsTo(other: E): Boolean = comparator(this, other)
        override fun E.hash(): Int = hasher(this)
    }

public fun <E> defaultHashing(): Hashing<E> = DefaultContext
public fun <E> absoluteHashing(): Hashing<E> = AbsoluteContext