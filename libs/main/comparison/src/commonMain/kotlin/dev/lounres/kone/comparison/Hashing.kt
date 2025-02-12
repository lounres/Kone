/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

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
public interface Hashing<in Element> {
    public fun Element.hash(): Int = this.hashCode()
    
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

context(_: KoneContextRegistry)
public fun <Element> loadHashingFor(elementType: SuppliedType<Element>): Hashing<Element> = load(Hashing.Key(elementType))
context(_: KoneContextRegistry)
public fun <Element> loadHashingForOrNull(elementType: SuppliedType<Element>): Hashing<Element>? = loadOrNull(Hashing.Key(elementType))
context(_: KoneContextRegistry)
public fun <Element> loadHashingForOrDefault(elementType: SuppliedType<Element>, default: Hashing<Element>): Hashing<Element> = loadOrDefault(Hashing.Key(elementType), default)
context(_: KoneContextRegistry)
public inline fun <Element> loadHashingForOrElse(elementType: SuppliedType<Element>, block: () -> Hashing<Element>): Hashing<Element> = loadOrElse(Hashing.Key(elementType), block)

public fun <Element> KoneContextRegistryBuilder.installDefaultHashingFor(suppliedElementType: SuppliedType<Element>) {
    contextsBuilder[Hashing.Key(suppliedElementType)] = defaultHashing<Element>()
}

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

public inline fun <Element, Result> defaultHashing(block: context(Hashing<Element>) () -> Result): Result = block(DefaultHashing)
public inline fun <Element, Result> absoluteHashing(block: context(Hashing<Element>) () -> Result): Result = block(DefaultHashing)