/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

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
    public class Key<Element>(
        elementType: SuppliedType,
    ) : RegistryKey<Hashing<Element>> {
        public val typeKey: SuppliedType.Regular =
            @OptIn(DelicateSuppliedTypeConstructor::class)
            SuppliedType.Regular(
                fullyQualifiedName = "dev.lounres.kone.relations.Hashing",
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
 * Shortcut for getting [Hashing] context for the given [suppliedElementType].
 * Throws if there is no such context in the registry.
 */
context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Element> Hashing.Companion.getFor(suppliedElementType: SuppliedType): Hashing<Element> =
    koneContextRegistryBuilder[Hashing.Key(suppliedElementType)]
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or `null` if there is no such context in the registry.
 */
context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Element> Hashing.Companion.getForOrNull(suppliedElementType: SuppliedType): Hashing<Element>? =
    koneContextRegistryBuilder.getOrNull(Hashing.Key(suppliedElementType))
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or [default] context if there is no such context in the registry.
 */
context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Element> Hashing.Companion.getForOrDefault(suppliedElementType: SuppliedType, default: Hashing<Element>): Hashing<Element> =
    koneContextRegistryBuilder.getOrDefault(Hashing.Key(suppliedElementType), default)
/**
 * Shortcut for getting [Hashing] context for the given [suppliedElementType]
 * or compute [block] to get such context if there is no such context in the registry.
 */
context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public inline fun <Element> Hashing.Companion.getForOrElse(suppliedElementType: SuppliedType, block: () -> Hashing<Element>): Hashing<Element> =
    koneContextRegistryBuilder.getOrElse(Hashing.Key(suppliedElementType), block)

/**
 * Sets default [Hashing] context for the given [suppliedElementType] into context registry builder.
 */
context(koneContextRegistryBuilder: RegistryBuilder<KoneContextRegistry>)
public fun <Element> Hashing.Companion.setDefaultFor(suppliedElementType: SuppliedType) {
    koneContextRegistryBuilder[Hashing.Key<Element>(suppliedElementType)] = Hashing.defaultFor<Element>()
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