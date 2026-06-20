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
 */
public interface Hashing<in Element> : KoneContext {
    /**
     * Computes hash code of [this] element.
     */
    public fun Element.hash(): Int = this.hashCode()
    
    public companion object;
    
    /**
     * Registry key for [Hashing] interface in [KoneContextRegistry].
     */
    @Suppliable
    public class Key<@Supply Element> : SuppliedTypeRegistryKey<Hashing<Element>>() {
        override fun toString(): String = "dev.lounres.kone.relations.Hashing.Key<${suppliedTypeOf<Element>()}>"
    }
}

/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType].
 * Throws if there is no such context in the registry.
 */
@Suppliable
context(koneContextRegistry: KoneContextRegistry)
public fun <@Supply Element> Hashing.Companion.getFor(): Hashing<Element> =
    koneContextRegistry[Hashing.Key()]
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or `null` if there is no such context in the registry.
 */
@Suppliable
context(koneContextRegistry: KoneContextRegistry)
public fun <@Supply Element> Hashing.Companion.getForOrNull(): Hashing<Element>? =
    koneContextRegistry.getOrNull(Hashing.Key())
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or [default] context if there is no such context in the registry.
 */
@Suppliable
context(koneContextRegistry: KoneContextRegistry)
public fun <@Supply Element> Hashing.Companion.getForOrDefault(default: Hashing<Element>): Hashing<Element> =
    koneContextRegistry.getOrDefault(Hashing.Key(), default)
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or compute [block] to get such context if there is no such context in the registry.
 */
@Suppliable
context(koneContextRegistry: KoneContextRegistry)
public inline fun <@Supply Element> Hashing.Companion.getForOrElse(block: () -> Hashing<Element>): Hashing<Element> =
    koneContextRegistry.getOrElse(Hashing.Key(), block)

/**
 * Sets default [Hashing] context for the given [suppliedElementType] into context registry builder.
 */
@Suppliable
context(_: MutableOwnedProviderRegistry<KoneContextRegistry>)
public fun <@Supply Element> Hashing.Companion.setDefaultFor() {
    Hashing.Key<Element>() correspondsTo Hashing.defaultFor<Element>()
}

/**
 * Computes a hash code of [this] element in the provided [Hashing] context.
 *
 * A bridge contextual function for [Hashing.hash].
 */
context(hashing: Hashing<Element>)
public fun <Element> Element.hash(): Int = with(hashing) { this@hash.hash() }

/**
 * [Hashing] builder from a [hasher] that computes hash of provided element.
 */
public inline fun <Element> Hashing(crossinline hasher: (Element) -> Int): Hashing<Element> =
    object : Hashing<Element> {
        override fun Element.hash(): Int = hasher(this)
    }

/**
 * Returns [Hashing] instance which [Equality.coincidesWith] operator just uses [Any.equals] operator's result as a return value
 * and which [Hashing.hash] operator just uses [Any.hashCode] operator's result as a return value.
 */
public fun <Element> Hashing.Companion.defaultFor(): Hashing<Element> = DefaultHashing