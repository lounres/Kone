/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.comparison

import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.context.load
import dev.lounres.kone.context.loadOrDefault
import dev.lounres.kone.context.loadOrElse
import dev.lounres.kone.context.loadOrNull
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


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
public interface Equality<in Element> {
    /**
     * Checks equality of [this] and [other] elements.
     */
    public infix fun Element.equalsTo(other: Element): Boolean = this == other
    
    public class Key<Element>(
        elementType: SuppliedType<Element>,
    ) : RegistryKey<Equality<Element>> {
        override val typeKey: SuppliedType.Regular<Equality<Element>> =
            SuppliedType.Regular(
                kClass = Equality::class,
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        KVariance.INVARIANT,
                        elementType
                    )
                ),
                isNullable = false
            )
    }
}

context(_: KoneContextRegistry)
public fun <Element> loadEqualityFor(elementType: SuppliedType<Element>): Equality<Element> = load(Equality.Key(elementType))
context(_: KoneContextRegistry)
public fun <Element> loadEqualityForOrNull(elementType: SuppliedType<Element>): Equality<Element>? = loadOrNull(Equality.Key(elementType))
context(_: KoneContextRegistry)
public fun <Element> loadEqualityForOrDefault(elementType: SuppliedType<Element>, default: Equality<Element>): Equality<Element> = loadOrDefault(Equality.Key(elementType), default)
context(_: KoneContextRegistry)
public inline fun <Element> loadEqualityForOrElse(elementType: SuppliedType<Element>, block: () -> Equality<Element>): Equality<Element> = loadOrElse(Equality.Key(elementType), block)

public fun <Element> KoneContextRegistryBuilder.installDefaultEqualityFor(suppliedElementType: SuppliedType<Element>) {
    contextsBuilder[Equality.Key(suppliedElementType)] = defaultEquality<Element>()
}
public fun <Element> KoneContextRegistryBuilder.installAbsoluteEqualityFor(suppliedElementType: SuppliedType<Element>) {
    contextsBuilder[Equality.Key(suppliedElementType)] = absoluteEquality<Element>()
}

/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A bridge contextual function for [Equality.equalsTo].
 */
// FIXME: KT-5351
context(equality: Equality<Element>)
public inline infix fun <Element> Element.equalsTo(other: Element): Boolean = with(equality) { this@equalsTo equalsTo other }
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [Equality.coincidesWith].
 */
// FIXME: KT-5351
context(_: Equality<Element>)
public inline infix fun <Element> Element.notEqualsTo(other: Element): Boolean = !(this@notEqualsTo equalsTo other)
/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for [Equality.coincidesWith].
 */
context(_: Equality<Element>)
public inline infix fun <Element> Element.eq(other: Element): Boolean = this equalsTo other
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [Equality.coincidesWith].
 */
// FIXME: KT-5351
context(_: Equality<Element>)
public inline infix fun <Element> Element.neq(other: Element): Boolean = !(this equalsTo other)

context(reification: Reification<Element>, _: Equality<Element>)
@Suppress("UNCHECKED_CAST")
public inline infix fun <Element> Any?.tryEqualsTo(other: Element): Boolean =
    if (this !in reification) false else (this as Element) equalsTo other

context(_: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryNotEqualsTo(other: Element): Boolean = !(this tryEqualsTo other)

context(_: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryEq(other: Element): Boolean = this tryEqualsTo other

context(_: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryNeq(other: Element): Boolean = !(this tryEqualsTo other)

/**
 * [Equality] builder from an [equalizer] that checks equality of the `left` and `right` elements.
 */
public inline fun <Element> Equality(crossinline equalizer: (left: Element, right: Element) -> Boolean): Equality<Element> =
    object : Equality<Element> {
        override fun Element.equalsTo(other: Element): Boolean = equalizer(this, other)
    }

/**
 * Returns [Equality] instance which [Equality.coincidesWith] operator just uses [Any.equals] operator's result as a return value.
 */
public fun <Element> defaultEquality(): Equality<Element> = DefaultEquality
/**
 * Returns [Equality] instance which [Equality.coincidesWith] operator just uses absolute equality `===` operator's result as a return value.
 */
public fun <Element> absoluteEquality(): Equality<Element> = AbsoluteEquality

public inline fun <Element, Result> defaultEquality(block: context(Equality<Element>) () -> Result): Result = block(DefaultEquality)
public inline fun <Element, Result> absoluteEquality(block: context(Equality<Element>) () -> Result): Result = block(AbsoluteEquality)