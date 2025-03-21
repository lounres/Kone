/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.relations

import dev.lounres.kone.contexts.KoneContext
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.contexts.KoneContextRegistryBuilder
import dev.lounres.kone.contexts.load
import dev.lounres.kone.contexts.loadOrDefault
import dev.lounres.kone.contexts.loadOrElse
import dev.lounres.kone.contexts.loadOrNull
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


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
    
    /**
     * Registry key for [Hashing] interface in [KoneContextRegistry].
     */
    public class Key<Element>(
        elementType: SuppliedType<Element>,
    ) : RegistryKey<Hashing<Element>> {
        override val typeKey: SuppliedType.Regular<Hashing<Element>> =
            SuppliedType.Regular(
                kClass = Hashing::class,
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

/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType].
 * Throws if there is no such context in the registry.
 */
public fun <Element> KoneContextRegistry.loadHashingFor(suppliedElementType: SuppliedType<Element>): Hashing<Element> = load(Hashing.Key(suppliedElementType))
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or `null` if there is no such context in the registry.
 */
public fun <Element> KoneContextRegistry.loadHashingForOrNull(suppliedElementType: SuppliedType<Element>): Hashing<Element>? = loadOrNull(Hashing.Key(suppliedElementType))
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or [default] context if there is no such context in the registry.
 */
public fun <Element> KoneContextRegistry.loadHashingForOrDefault(suppliedElementType: SuppliedType<Element>, default: Hashing<Element>): Hashing<Element> = loadOrDefault(Hashing.Key(suppliedElementType), default)
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or compute [block] to get such context if there is no such context in the registry.
 */
public inline fun <Element> KoneContextRegistry.loadHashingForOrElse(suppliedElementType: SuppliedType<Element>, block: () -> Hashing<Element>): Hashing<Element> = loadOrElse(Hashing.Key(suppliedElementType), block)

/**
 * Installs default [Hashing] context for the given [suppliedElementType] into context registry builder.
 */
public fun <Element> KoneContextRegistryBuilder.installDefaultHashingFor(suppliedElementType: SuppliedType<Element>) {
    contextsBuilder[Hashing.Key(suppliedElementType)] = defaultHashing<Element>()
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
public fun <Element> defaultHashing(): Hashing<Element> = DefaultHashing