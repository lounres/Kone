/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.comparison

import dev.lounres.kone.context.KoneContext


/**
 * Describes a context that provides equality [equivalence relation](https://en.wikipedia.org/wiki/Equivalence_relation)
 * on the set of elements of type [Element]. The relation is described by [equalsTo] function.
 *
 * Such contexts are used instead of usual [equals] overloading for several reasons. Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Equality<in Element>: KoneContext {
    /**
     * Checks equality of [this] and [other] elements.
     */
    public infix fun Element.equalsTo(other: Element): Boolean = this == other
}

public interface ReifiedEquality<Element> : Reification<Element>, Equality<Element>

/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A bridge contextual function for [Equality.equalsTo].
 */
// FIXME: KT-5351
context(equality: Equality<Element>)
public inline infix fun <Element> Element.equalsTo(other: Element): Boolean = with(equality) { this@equalsTo equalsTo other }
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [Equality.equalsTo].
 */
// FIXME: KT-5351
context(_: Equality<Element>)
public inline infix fun <Element> Element.notEqualsTo(other: Element): Boolean = !(this@notEqualsTo equalsTo other)
/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for [Equality.equalsTo].
 */
context(_: Equality<Element>)
public inline infix fun <Element> Element.eq(other: Element): Boolean = this equalsTo other
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [Equality.equalsTo].
 */
// FIXME: KT-5351
context(_: Equality<Element>)
public inline infix fun <Element> Element.neq(other: Element): Boolean = !(this equalsTo other)

context(reifiedEquality: ReifiedEquality<Element>)
@Suppress("UNCHECKED_CAST")
public inline infix fun <Element> Any?.tryEqualsTo(other: Element): Boolean =
    if (this !in reifiedEquality) false else (this as Element) equalsTo other

context(_: ReifiedEquality<Element>)
public inline infix fun <Element> Any?.tryNotEqualsTo(other: Element): Boolean = !(this tryEqualsTo other)

context(_: ReifiedEquality<Element>)
public inline infix fun <Element> Any?.tryEq(other: Element): Boolean = this tryEqualsTo other

context(_: ReifiedEquality<Element>)
public inline infix fun <Element> Any?.tryNeq(other: Element): Boolean = !(this tryEqualsTo other)

/**
 * [Equality] builder from an [equalizer] that checks equality of the `left` and `right` elements.
 */
public inline fun <Element> Equality(crossinline equalizer: (left: Element, right: Element) -> Boolean): Equality<Element> =
    object : Equality<Element> {
        override fun Element.equalsTo(other: Element): Boolean = equalizer(this, other)
    }

/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses [Any.equals] operator's result as a return value.
 */
public fun <Element> defaultEquality(): Equality<Element> = DefaultContext
/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses absolute equality `===` operator's result as a return value.
 */
public fun <Element> absoluteEquality(): Equality<Element> = AbsoluteContext

public inline fun <reified Element> defaultReifiedEquality(): ReifiedEquality<Element> = defaultReifiedHashing()
public inline fun <reified Element> absoluteReifiedEquality(): ReifiedEquality<Element> = absoluteReifiedHashing()

public inline fun <Element, Result> defaultEquality(block: context(Equality<Element>) () -> Result): Result = block(DefaultContext)
public inline fun <Element, Result> absoluteEquality(block: context(Equality<Element>) () -> Result): Result = block(AbsoluteContext)

public inline fun <reified Element, Result> defaultReifiedEquality(block: context(ReifiedEquality<Element>) () -> Result): Result = block(defaultReifiedHashing())
public inline fun <reified Element, Result> absoluteReifiedEquality(block: context(ReifiedEquality<Element>) () -> Result): Result = block(absoluteReifiedHashing())