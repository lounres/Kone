/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.relations

import dev.lounres.kone.context.KoneContext


public interface Equality<in E>: KoneContext {
    public infix fun E.equalsTo(other: E): Boolean = this == other
}

context(Equality<E>)
public infix fun <E> E.notEqualsTo(other: E): Boolean = !(this equalsTo other)
context(Equality<E>)
public infix fun <E> E.eq(other: E): Boolean = this equalsTo other
context(Equality<E>)
public infix fun <E> E.neq(other: E): Boolean = !(this equalsTo other)