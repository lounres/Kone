/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.comparison

import dev.lounres.kone.context.KoneContext


public interface Equality<in E>: KoneContext {
    public infix fun E.equalsTo(other: E): Boolean = this == other
    // FIXME: KT-5351
    public infix fun E.notEqualsTo(other: E): Boolean = !(this equalsTo other)
    public infix fun E.eq(other: E): Boolean = this equalsTo other
    // FIXME: KT-5351
    public infix fun E.neq(other: E): Boolean = !(this equalsTo other)
}

public inline fun <E> Equality(crossinline comparator: (left: E, right: E) -> Boolean): Equality<E> =
    object : Equality<E> {
        override fun E.equalsTo(other: E): Boolean = comparator(this, other)
    }

public fun <E> defaultEquality(): Equality<E> = DefaultContext
public fun <E> absoluteEquality(): Equality<E> = AbsoluteContext