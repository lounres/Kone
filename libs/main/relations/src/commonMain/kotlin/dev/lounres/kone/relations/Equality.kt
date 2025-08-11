/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.getOrDefault
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


/**
 * Describes a context that provides equality [equivalence relation](https://en.wikipedia.org/wiki/Equivalence_relation)
 * on the set of elements of type [Element] (or some subdomain). The relation is described by [equalsTo] function.
 *
 * Such contexts are used instead of usual [equals] overloading for several reasons. Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 */
public interface Equality<in Element> : KoneContext {
    /**
     * Checks equality of [this] and [other] elements.
     */
    public infix fun Element.equalsTo(other: Element): Boolean = this == other
    
    /**
     * Registry key for [Equality] interface in [KoneContextRegistry].
     */
    public class Key<Element>(
        elementType: SuppliedType,
    ) : RegistryKey<Equality<Element>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.relations.Equality",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = IN,
                        type = elementType
                    )
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
    }
}

/**
 * Shortcut for getting [Equality] context for the given [suppliedElementType].
 * Throws if there is no such context in the registry.
 */
public fun <Element> KoneContextRegistry.getEqualityFor(suppliedElementType: SuppliedType): Equality<Element> = get(Equality.Key(suppliedElementType))
/**
 * Shortcut for getting [Equality] context for the given [suppliedElementType]
 * or `null` if there is no such context in the registry.
 */
public fun <Element> KoneContextRegistry.getEqualityForOrNull(suppliedElementType: SuppliedType): Equality<Element>? = getOrNull(Equality.Key(suppliedElementType))
/**
 * Shortcut for getting [Equality] context for the given [suppliedElementType]
 * or [default] context if there is no such context in the registry.
 */
public fun <Element> KoneContextRegistry.getEqualityForOrDefault(suppliedElementType: SuppliedType, default: Equality<Element>): Equality<Element> = getOrDefault(Equality.Key(suppliedElementType), default)
/**
 * Shortcut for getting [Equality] context for the given [suppliedElementType]
 * or compute [block] to get such context if there is no such context in the registry.
 */
public inline fun <Element> KoneContextRegistry.getEqualityForOrElse(suppliedElementType: SuppliedType, block: () -> Equality<Element>): Equality<Element> = getOrElse(Equality.Key(suppliedElementType), block)

/**
 * Sets default [Equality] context for the given [suppliedElementType] into context registry builder.
 */
public fun <Element> RegistryBuilder<KoneContextRegistry>.setDefaultEqualityFor(suppliedElementType: SuppliedType) {
    Equality.Key<Element>(suppliedElementType) correspondsTo  defaultEquality<Element>()
}
/**
 * Sets absolute [Equality] context for the given [suppliedElementType] into context registry builder.
 */
public fun <Element> RegistryBuilder<KoneContextRegistry>.setAbsoluteEqualityFor(suppliedElementType: SuppliedType) {
    Equality.Key<Element>(suppliedElementType) correspondsTo  absoluteEquality<Element>()
}

/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 *
 * A bridge contextual function for [Equality.equalsTo].
 */
// FIXME: KT-5351
context(equality: Equality<Element>)
public inline infix fun <Element> Element.equalsTo(other: Element): Boolean = with(equality) { this@equalsTo equalsTo other }
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [equalsTo].
 */
// FIXME: KT-5351
context(_: Equality<Element>)
public inline infix fun <Element> Element.notEqualsTo(other: Element): Boolean = !(this@notEqualsTo equalsTo other)
/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for [equalsTo].
 */
context(_: Equality<Element>)
public inline infix fun <Element> Element.eq(other: Element): Boolean = this equalsTo other
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [equalsTo].
 */
// FIXME: KT-5351
context(_: Equality<Element>)
public inline infix fun <Element> Element.neq(other: Element): Boolean = !(this equalsTo other)

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is equal to right-hand side.
 */
@Suppress("UNCHECKED_CAST")
context(reification: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryEqualsTo(other: Element): Boolean =
    if (this !in reification) false else (this as Element) equalsTo other

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is not equal to right-hand side.
 */
@Suppress("UNCHECKED_CAST")
context(reification: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryNotEqualsTo(other: Element): Boolean =
    if (this !in reification) false else (this as Element) notEqualsTo other

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is equal to right-hand side. A shortcut for [tryEqualsTo].
 */
context(_: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryEq(other: Element): Boolean = this tryEqualsTo other

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is not equal to right-hand side. A shortcut for [tryNotEqualsTo].
 */
context(_: Reification<Element>, _: Equality<Element>)
public inline infix fun <Element> Any?.tryNeq(other: Element): Boolean = this tryNotEqualsTo other

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
public fun <Element> defaultEquality(): Equality<Element> = DefaultEquality
/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses absolute equality `===` operator's result as a return value.
 */
public fun <Element> absoluteEquality(): Equality<Element> = AbsoluteEquality