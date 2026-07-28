/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Describes a context that provides [hash] operator besides inherited [coincidesWith] operator. This operator should return
 * the same value for elements that are equal according to [coincidesWith] operator.
 *
 * Such contexts are used instead of usual [hashCode] overloading for several reasons. Some of them are:
 * - Following structural pattern, any behaviour *between* elements should not be a part of the elements' logic
 *   but a part of assumed context. (For example, summing two integers together, we assume that we are summing them
 *   as two integer, but not as a residue of some modulo.)
 * - Such separation of entities and operations over them brings modularity: you can change operations context
 *   leaving the entities the same.
 *
 * @param Element The type of elements for which this hashing context is defined.
 */
public interface Hashing<in Element> : KoneContext {
    /**
     * Computes hash code of [this] element.
     *
     * @receiver The element for which to compute the hash code.
     * @return The hash code value for this element.
     */
    public fun Element.hash(): Int = this.hashCode()
    
    /**
     * Companion object for [Hashing] interface providing factory methods
     * for creating [Hashing] instances.
     */
    public companion object;
    
    /**
     * Registry key for [Hashing] interface in [KoneContextRegistry].
     *
     * This key is used to register and retrieve [Hashing] instances for specific types
     * in the context registry.
     *
     * @param Element The type of elements for which this hashing context is registered.
     */
    @Suppliable
    public class Key<@Supply Element> : SuppliedTypeRegistryKey<Hashing<Element>>() {
        override fun toString(): String = "dev.lounres.kone.relations.Hashing.Key<${suppliedTypeOf<Element>()}>"
    }
}


/**
 * Computes a hash code of [this] element in the provided [Hashing] context.
 *
 * A bridge contextual function for [Hashing.hash].
 *
 * @param Element The type of elements for which to compute the hash code.
 * @receiver The element for which to compute the hash code.
 * @param hashing The hashing context in which to compute the hash code.
 * @return The hash code value for this element according to the provided hashing context.
 */
context(hashing: Hashing<Element>)
public fun <Element> Element.hash(): Int = with(hashing) { this@hash.hash() }

/**
 * [Hashing] builder from a [hasher] that computes hash of provided element.
 *
 * Creates a custom [Hashing] context using the provided hashing logic.
 *
 * @param Element The type of elements this hashing context will handle.
 * @param hasher A function that takes an element and returns its hash code.
 * @return A [Hashing] instance that uses the provided [hasher] function for hash code computation.
 */
public inline fun <Element> Hashing(crossinline hasher: (Element) -> Int): Hashing<Element> =
    object : Hashing<Element> {
        override fun Element.hash(): Int = hasher(this)
    }

/**
 * Returns [Hashing] instance which [Hashing.hash] operator just uses [Any.hashCode] operator's result as a return value.
 *
 * This provides a default hashing context that uses the standard [Any.hashCode] method.
 *
 * @param Element The type of elements for which to create the default hashing context.
 * @return A [Hashing] instance that uses the standard hash code computation.
 */
public fun <Element> Hashing.Companion.defaultFor(): Hashing<Element> = DefaultHashing

/**
 * Container object for suppliable top-level functions related to [Hashing] context registration.
 *
 * These functions are used within DSL builders to register default hashing contexts.
 */
// TODO: Remove the checker when KT-73135 will be fixed
public object HashingSuppliableTopLevelFunctions {
    /**
     * Sets default [Hashing] context for the given supplied type [Element] into context registry builder.
     *
     * The registered hashing context uses the standard [Any.hashCode] method for hash computation.
     *
     * @receiver The hashing companion object.
     * @param registry The mutable owned provider registry to register into.
     * @param Element The supplied element type for which to set the default hashing context.
     */
    @Suppliable
    context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public fun <@Supply Element> Hashing.Companion.setDefaultFor() {
        Hashing.Key<Element>() correspondsTo Hashing.defaultFor<Element>()
    }
}

/**
 * Returns a [Hashing] context for nullable elements based on this non-nullable hashing context.
 *
 * The resulting hashing context handles null values by returning 0 for null,
 * and delegates to this hashing context for non-null values.
 *
 * @param Element The non-nullable element type for which to create a nullable hashing context.
 * @receiver The hashing context for non-nullable elements.
 * @return A [Hashing] instance that can handle nullable elements.
 */
public val <Element: Any> Hashing<Element>.nullable: Hashing<Element?> get() = Hashing {
    if (it == null) 0 else with(this) { it.hash() }
}