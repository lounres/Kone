/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some
import dev.lounres.kone.registry.*
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf

/**
 * Describes a context that checks if the element lays in specific domain.
 * It is usually used in pair with contexts like [Equality], [Order], or [Hashing]
 * to check that the element lays in the second context's domain.
 *
 * For example, it is needed for covariant sets/maps.
 * Without it, methods like `KoneSet.contains` cannot be covariant.
 */
public interface Reification<out Element> : KoneContext {
    /**
     * Checks if the [element] lays in described by this instance domain.
     *
     * @param element The element to check for domain membership.
     * @return `true` if the element is part of this reification's domain, `false` otherwise.
     */
    public operator fun contains(element: Any?): Boolean
    /**
     * Checks if the [element] lays in described by this instance domain,
     * and if the element does lay in the domain, returns [Some] of it,
     * otherwise returns [None].
     *
     * @param element The element to reify.
     * @return [Some] containing the element if it is in the domain, [None] otherwise.
     */
    public fun reifyMaybe(element: Any?): Maybe<Element>
    /**
     * Checks if the [element] lays in described by this instance domain,
     * and if the element does lay in the domain, returns it,
     * otherwise returns null.
     *
     * @param element The element to reify.
     * @return The element cast to [Element] if it is in the domain, null otherwise.
     */
    public fun reifyOrNull(element: Any?): Element?
    /**
     * Checks if the [element] lays in described by this instance domain,
     * and if the element does lay in the domain, returns it,
     * otherwise throws [ReificationException].
     *
     * @param element The element to reify.
     * @return The element cast to [Element] if it is in the domain.
     * @throws ReificationException iff the element is not a part of the described domain.
     */
    public fun reify(element: Any?): Element
    
    /**
     * Companion object for [Reification] interface providing factory methods
     * for creating [Reification] instances.
     */
    public companion object;
    
    /**
     * Registry key for [Reification] interface in [KoneContextRegistry].
     *
     * This key is used to register and retrieve [Reification] instances for specific types
     * in the context registry.
     *
     * @param Element The type of elements for which this reification context is registered.
     */
    @Suppliable
    public class Key<@Supply Element> : SuppliedTypeRegistryKey<Reification<Element>>() {
        override fun toString(): String = "dev.lounres.kone.relations.Reification.Key<${suppliedTypeOf<Element>()}>"
    }
}

/**
 * Describes that element was forcefully (via [Reification.reify]) checked on lying in the domain,
 * and the check was unsuccessful.
 *
 * This exception is thrown when attempting to reify an element that does not belong to the expected domain.
 *
 * @param message The detail message explaining why the reification failed.
 */
public class ReificationException(message: String) : RuntimeException(message)

/**
 * Throws [ReificationException] with the provided [message].
 *
 * This is a convenience function for throwing reification exceptions with custom messages.
 *
 * @param message The message to include in the exception. Defaults to "Value can not be reified".
 * @return Nothing - this function always throws an exception.
 * @throws ReificationException Always throws with the provided message.
 */
public fun reificationException(message: String = "Value can not be reified"): Nothing = throw ReificationException(message)

/**
 * Checks if the [element] lays in described by this instance domain,
 * and if the element does lay in the domain, returns [Some] of it,
 * otherwise returns [None].
 *
 * A bridge contextual function for [Reification.reifyMaybe].
 *
 * @param Element The type of elements this reification context handles.
 * @param reification The reification context to use for checking and casting the element.
 * @param element The element to reify.
 * @return [Some] containing the element if it is in the domain, [None] otherwise.
 */
context(reification: Reification<Element>)
public fun <Element> reifyMaybe(element: Any?): Maybe<Element> = reification.reifyMaybe(element)
/**
 * Checks if the [element] lays in described by this instance domain,
 * and if the element does lay in the domain, returns it,
 * otherwise returns null.
 *
 * A bridge contextual function for [Reification.reifyOrNull].
 *
 * @param Element The type of elements this reification context handles.
 * @param reification The reification context to use for checking and casting the element.
 * @param element The element to reify.
 * @return The element cast to [Element] if it is in the domain, null otherwise.
 */
context(reification: Reification<Element>)
public fun <Element> reifyOrNull(element: Any?): Element? = reification.reifyOrNull(element)
/**
 * Checks if the [element] lays in described by this instance domain,
 * and if the element does lay in the domain, returns it,
 * otherwise throws [ReificationException].
 *
 * A bridge contextual function for [Reification.reify].
 *
 * @param Element The type of elements this reification context handles.
 * @param reification The reification context to use for checking and casting the element.
 * @param element The element to reify.
 * @return The element cast to [Element] if it is in the domain.
 * @throws ReificationException iff the element is not a part of the described domain.
 */
context(reification: Reification<Element>)
public fun <Element> reify(element: Any?): Element = reification.reify(element)

///**
// * [Reification] builder from a reified type [Element] that is used to cast elements.
// */
//public fun <Element : Any> Reification(reificationClass: KClass<Element>): Reification<Element> =
//    object : Reification<Element> {
//        override fun contains(element: Any?): Boolean = reificationClass.isInstance(element)
//        override fun reifyMaybe(element: Any?): Maybe<Element> =
//            if (reificationClass.isInstance(element)) Some(element as Element) else None
//        override fun reifyOrNull(element: Any?): Element? =
//            if (reificationClass.isInstance(element)) element as Element else null
//        override fun reify(element: Any?): Element =
//            if (reificationClass.isInstance(element)) element as Element else reificationException()
//    }

/**
 * [Reification] builder from a reified type [Element] that is used to cast elements.
 *
 * Creates a reification context that checks if elements are of the specified type [Element]
 * using Kotlin's reified type checks.
 *
 * @receiver The reification companion object.
 * @param Element The reified type to check against.
 * @return A [Reification] instance that uses type checking for domain verification.
 */
public inline fun <reified Element> Reification.Companion.defaultFor(): Reification<Element> =
    object : Reification<Element> {
        override fun contains(element: Any?): Boolean = element is Element
        override fun reifyMaybe(element: Any?): Maybe<Element> = if (element is Element) Some(element) else None
        override fun reifyOrNull(element: Any?): Element? = element as? Element
        override fun reify(element: Any?): Element = if (element is Element) element else reificationException()
    }
// TODO: Remove the checker when KT-73135 will be fixed
/**
 * Container object for suppliable top-level functions related to [Reification] context registration.
 *
 * These functions are used within DSL builders to register default reification contexts.
 */
public object ReificationSuppliableTopLevelFunctions {
    /**
     * Sets [Reification] context for the given [suppliedElementType] into context registry builder.
     * The set reification just only checks that the element is of type [Element].
     *
     * @receiver The reification companion object.
     * @param registry The mutable owned provider registry to register into.
     * @param Element The supplied element type for which to set the reification context.
     */
    @Suppliable
    context(registry: MutableOwnedProviderRegistry<KoneContextRegistry>)
    public inline fun <@Supply reified Element> Reification.Companion.setDefaultFor() {
        Reification.Key<Element>() correspondsTo RegisteredValueProvider.cached { Reification.defaultFor<Element>() }
    }
}