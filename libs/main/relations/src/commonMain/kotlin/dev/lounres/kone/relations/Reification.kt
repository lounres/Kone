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
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.RegisteredValueProvider
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.cached
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.get
import dev.lounres.kone.registry.getOrDefault
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.registry.getOrNull
import dev.lounres.kone.suppliedTypes.DelicateSuppliedTypeConstructor
import dev.lounres.kone.suppliedTypes.SuppliedProjection
import dev.lounres.kone.suppliedTypes.SuppliedType

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
     */
    public operator fun contains(element: Any?): Boolean
    /**
     * Checks if the [element] lays in described by this instance domain,
     * and if the element does lay in the domain, returns [Some] of it,
     * otherwise returns [None].
     */
    public fun reifyMaybe(element: Any?): Maybe<Element>
    /**
     * Checks if the [element] lays in described by this instance domain,
     * and if the element does lay in the domain, returns it,
     * otherwise returns null.
     */
    public fun reifyOrNull(element: Any?): Element?
    /**
     * Checks if the [element] lays in described by this instance domain,
     * and if the element does lay in the domain, returns it,
     * otherwise throws [ReificationException].
     *
     * @throws ReificationException iff the element is not a part of the described domain.
     */
    public fun reify(element: Any?): Element
    
    public companion object;
    
    /**
     * Registry key for [Reification] interface in [KoneContextRegistry].
     */
    public class Key<Element>(
        public val elementType: SuppliedType,
    ) : RegistryKey<Reification<Element>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.relations.Reification",
                typeArguments = listOf(
                    SuppliedProjection.Regular(
                        variance = OUT,
                        type = elementType
                    )
                ),
                isNullable = false
            )
        override fun equals(other: Any?): Boolean = other is Key<*> && typeKey == other.typeKey
        override fun hashCode(): Int = typeKey.hashCode()
        override fun toString(): String = "dev.lounres.kone.relations.Reification.Key<$elementType>"
    }
}

/**
 * Shortcut for getting [Reification] context for the given [suppliedElementType].
 * Throws if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> Reification.Companion.getFor(suppliedElementType: SuppliedType): Reification<Element> =
    koneContextRegistry[Reification.Key(suppliedElementType)]
/**
 * Shortcut for getting [Reification] context for the given [suppliedElementType]
 * or `null` if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> Reification.Companion.getForOrNull(suppliedElementType: SuppliedType): Reification<Element>? =
    koneContextRegistry.getOrNull(Reification.Key(suppliedElementType))
/**
 * Shortcut for getting [Reification] context for the given [suppliedElementType]
 * or [default] context if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public fun <Element> Reification.Companion.getForOrDefault(suppliedElementType: SuppliedType, default: Reification<Element>): Reification<Element> =
    koneContextRegistry.getOrDefault(Reification.Key(suppliedElementType), default)
/**
 * Shortcut for getting [Reification] context for the given [suppliedElementType]
 * or compute [block] to get such context if there is no such context in the registry.
 */
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Element> Reification.Companion.getForOrElse(suppliedElementType: SuppliedType, block: () -> Reification<Element>): Reification<Element> =
    koneContextRegistry.getOrElse(Reification.Key(suppliedElementType), block)

/**
 * Sets [Reification] context for the given [suppliedElementType] into context registry builder.
 * The set reification just only checks that the element is of type [Element].
 */
context(_: MutableOwnedRegistry<KoneContextRegistry>)
public inline fun <reified Element> Reification.Companion.setDefaultFor(suppliedElementType: SuppliedType) {
    Reification.Key<Element>(suppliedElementType) correspondsTo RegisteredValueProvider.cached { Reification.defaultFor<Element>() }
}

/**
 * Describes that element was forcefully (via [Reification.reify]) checked on lying in the domain,
 * and the check was unsuccessful.
 */
public class ReificationException(message: String) : RuntimeException(message)

/**
 * Throws [ReificationException] with the provided [message].
 */
public fun reificationException(message: String = "Value can not be reified"): Nothing = throw ReificationException(message)

/**
 * Checks if the [element] lays in described by this instance domain,
 * and if the element does lay in the domain, returns [Some] of it,
 * otherwise returns [None].
 *
 * A bridge contextual function for [Reification.reifyMaybe].
 */
context(reification: Reification<Element>)
public fun <Element> reifyMaybe(element: Any?): Maybe<Element> = reification.reifyMaybe(element)
/**
 * Checks if the [element] lays in described by this instance domain,
 * and if the element does lay in the domain, returns it,
 * otherwise returns null.
 *
 * A bridge contextual function for [Reification.reifyOrNull].
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
 */
public inline fun <reified Element> Reification.Companion.defaultFor(): Reification<Element> =
    object : Reification<Element> {
        override fun contains(element: Any?): Boolean = element is Element
        override fun reifyMaybe(element: Any?): Maybe<Element> = if (element is Element) Some(element) else None
        override fun reifyOrNull(element: Any?): Element? = element as? Element
        override fun reify(element: Any?): Element = if (element is Element) element else reificationException()
    }