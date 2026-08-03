/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE")

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.contextsKeys.GenerateKoneContextKey
import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply


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
 *
 * @param Element The type of elements for which this equality context is defined.
 */
@GenerateKoneContextKey
public interface Equality<in Element> : KoneContext {
    /**
     * Checks equality of [this] and [other] elements.
     *
     * @receiver The first element to compare.
     * @param other The second element to compare.
     * @return `true` if this element equals the other element according to this equality context,
     *         `false` otherwise.
     */
    public infix fun Element.equalsTo(other: Element): Boolean = this == other
    
    /**
     * Companion object for [Equality] interface providing factory methods
     * for creating [Equality] instances.
     */
    public companion object;
}

/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 *
 * A bridge contextual function for [Equality.equalsTo].
 *
 * @param Element The type of elements being compared.
 * @receiver The element to check for equality.
 * @param equality The equality context in which to perform the comparison.
 * @param other The element to compare with this element.
 * @return `true` if this element equals the other element according to the provided equality context,
 *         `false` otherwise.
 */
context(equality: Equality<Element>)
public inline infix fun <Element> Element.equalsTo(other: Element): Boolean = with(equality) { this@equalsTo equalsTo other }

/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [equalsTo].
 *
 * @param Element The type of elements being compared.
 * @receiver The element to check for inequality.
 * @param equality The equality context in which to perform the comparison.
 * @param other The element to compare with this element.
 * @return `true` if this element does not equal the other element according to the provided equality context,
 *         `false` otherwise.
 */
// FIXME: KT-5351
context(equality: Equality<Element>)
public inline infix fun <Element> Element.notEqualsTo(other: Element): Boolean = !(this equalsTo other)

/**
 * Checks equality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for [equalsTo].
 *
 * @param Element The type of elements being compared.
 * @receiver The element to check for equality.
 * @param equality The equality context in which to perform the comparison.
 * @param other The element to compare with this element.
 * @return `true` if this element equals the other element according to the provided equality context,
 *         `false` otherwise.
 */
context(equality: Equality<Element>)
public inline infix fun <Element> Element.eq(other: Element): Boolean = this equalsTo other
/**
 * Checks inequality of [this] and [other] elements in the provided [Equality] context.
 * A shortcut for negation of [equalsTo].
 *
 * @param Element The type of elements being compared.
 * @receiver The element to check for inequality.
 * @param equality The equality context in which to perform the comparison.
 * @param other The element to compare with this element.
 * @return `true` if this element does not equal the other element according to the provided equality context,
 *         `false` otherwise.
 */
// FIXME: KT-5351
context(equality: Equality<Element>)
public inline infix fun <Element> Element.neq(other: Element): Boolean = !(this equalsTo other)

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is equal to right-hand side.
 *
 * @receiver The left-hand side element to check.
 * @param Element The type of elements for which this check is performed.
 * @param reification The reification context to check if the receiver lies in the domain.
 * @param equality The equality context in which to perform the comparison.
 * @param other The right-hand side element to compare with.
 * @return `true` if the receiver lies in the domain and equals the other element,
 *         `false` otherwise.
 */
@Suppress("UNCHECKED_CAST")
context(reification: Reification<Element>, equality: Equality<Element>)
public inline infix fun <Element> Any?.tryEqualsTo(other: Element): Boolean = this in reification && (this as Element) equalsTo other

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is not equal to right-hand side.
 *
 * @receiver The left-hand side element to check.
 * @param Element The type of elements for which this check is performed.
 * @param reification The reification context to check if the receiver lies in the domain.
 * @param equality The equality context in which to perform the comparison.
 * @param other The right-hand side element to compare with.
 * @return `true` if the receiver lies in the domain and does not equal the other element,
 *         `false` otherwise.
 */
@Suppress("UNCHECKED_CAST")
context(reification: Reification<Element>, equality: Equality<Element>)
public inline infix fun <Element> Any?.tryNotEqualsTo(other: Element): Boolean = this in reification && (this as Element) notEqualsTo other

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is equal to right-hand side. A shortcut for [tryEqualsTo].
 *
 * @receiver The left-hand side element to check.
 * @param Element The type of elements for which this check is performed.
 * @param reification The reification context to check if the receiver lies in the domain.
 * @param equality The equality context in which to perform the comparison.
 * @param other The right-hand side element to compare with.
 * @return `true` if the receiver lies in the domain and equals the other element,
 *         `false` otherwise.
 */
context(reification: Reification<Element>, equality: Equality<Element>)
public inline infix fun <Element> Any?.tryEq(other: Element): Boolean = this tryEqualsTo other

/**
 * Check that the left-hand side lies in domain of provided [Equality] (via provided [Reification])
 * and is not equal to right-hand side. A shortcut for [tryNotEqualsTo].
 *
 * @receiver The left-hand side element to check.
 * @param Element The type of elements for which this check is performed.
 * @param reification The reification context to check if the receiver lies in the domain.
 * @param equality The equality context in which to perform the comparison.
 * @param other The right-hand side element to compare with.
 * @return `true` if the receiver lies in the domain and does not equal the other element,
 *         `false` otherwise.
 */
context(reification: Reification<Element>, equality: Equality<Element>)
public inline infix fun <Element> Any?.tryNeq(other: Element): Boolean = this tryNotEqualsTo other

/**
 * [Equality] builder from an [equalizer] that checks equality of the `left` and `right` elements.
 *
 * Creates a custom [Equality] context using the provided comparison logic.
 *
 * @param Element The type of elements this equality context will handle.
 * @param equalizer A function that takes two elements and returns `true` if they are equal.
 * @return An [Equality] instance that uses the provided [equalizer] function for equality checks.
 */
public inline fun <Element> Equality(crossinline equalizer: (left: Element, right: Element) -> Boolean): Equality<Element> =
    object : Equality<Element> {
        override fun Element.equalsTo(other: Element): Boolean = equalizer(this, other)
    }

/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses [Any.equals] operator's result as a return value.
 *
 * This provides structural equality semantics based on the standard [Any.equals] method.
 *
 * @param Element The type of elements for which to create the default equality context.
 * @return An [Equality] instance that uses structural equality (`==`) for comparisons.
 */
public fun <Element> Equality.Companion.defaultFor(): Equality<Element> = DefaultEquality

/**
 * Returns [Equality] instance which [Equality.equalsTo] operator just uses absolute equality `===` operator's result as a return value.
 *
 * This provides referential equality semantics, where two elements are equal only if they are the exact same instance.
 *
 * @param Element The type of elements for which to create the absolute equality context.
 * @return An [Equality] instance that uses referential equality (`===`) for comparisons.
 */
public fun <Element> Equality.Companion.absoluteFor(): Equality<Element> = AbsoluteEquality

/**
 * Container object for suppliable top-level functions related to [Equality] context registration.
 *
 * These functions are used within DSL builders to register default equality contexts.
 */
// TODO: Remove the checker when KT-73135 will be fixed
public object EqualitySuppliableTopLevelFunctions {
    /**
     * Sets default [Equality] context for the given supplied type [Element] into context registry builder.
     *
     * The registered equality context uses structural equality (`==`) for comparisons.
     *
     * @receiver The equality companion object.
     * @param registry The mutable owned provider registry to register into.
     * @param Element The supplied element type for which to set the default equality context.
     */
    @Suppliable
    context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Element> Equality.Companion.setDefaultFor() {
        Equality.Key<Element>() correspondsTo Equality.defaultFor<Element>()
    }
    /**
     * Sets absolute [Equality] context for the given supplied type [Element] into context registry builder.
     *
     * The registered equality context uses referential equality (`===`) for comparisons.
     *
     * @receiver The equality companion object.
     * @param registry The mutable owned provider registry to register into.
     * @param Element The supplied element type for which to set the absolute equality context.
     */
    @Suppliable
    context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Element> Equality.Companion.setAbsoluteFor() {
        Equality.Key<Element>() correspondsTo Equality.absoluteFor<Element>()
    }
}
/**
 * Returns [Equality] instance which [Equality.equalsTo] operator always returns `true`.
 *
 * This equality context considers all elements equal to each other.
 *
 * @param Element The type of elements for which to create the always-accepting equality context.
 * @return An [Equality] instance that always returns `true` for any comparison.
 */
public fun <Element> Equality.Companion.alwaysAcceptingFor(): Equality<Element> = AllwaysAcceptingEquality
/**
 * Returns [Equality] instance which [Equality.equalsTo] operator always returns `false`.
 *
 * This equality context considers no elements equal to each other.
 *
 * @param Element The type of elements for which to create the always-denying equality context.
 * @return An [Equality] instance that always returns `false` for any comparison.
 */
public fun <Element> Equality.Companion.alwaysDenyingFor(): Equality<Element> = AllwaysDenyingEquality

/**
 * Returns an [Equality] context for nullable elements based on this non-nullable equality context.
 *
 * The resulting equality context handles null values appropriately:
 * - Two null values are considered equal
 * - A null value and a non-null value are considered not equal
 * - Two non-null values are compared using this equality context
 *
 * @param Element The non-nullable element type for which to create a nullable equality context.
 * @receiver The equality context for non-nullable elements.
 * @return An [Equality] instance that can handle nullable elements.
 */
public val <Element: Any> Equality<Element>.nullable: Equality<Element?> get() = Equality { left, right ->
    when {
        left == null && right == null -> true
        left == null || right == null -> false
        else -> this { left eq right }
    }
}