/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.comparison

import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.context.KoneContextRegistryBuilder
import dev.lounres.kone.context.load
import dev.lounres.kone.context.loadOrDefault
import dev.lounres.kone.context.loadOrElse
import dev.lounres.kone.context.loadOrNull
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some
import dev.lounres.kone.util.registry.RegistryKey
import dev.lounres.kone.util.suppliedTypes.SuppliedProjection
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.reflect.KVariance


public interface Reification<out Element> {
    public operator fun contains(element: Any?): Boolean
    public fun reifyMaybe(element: Any?): Maybe<Element>
    public fun reifyOrNull(element: Any?): Element?
    public fun reify(element: Any?): Element
    
    public class Key<Element>(
        elementType: SuppliedType<Element>,
    ) : RegistryKey<Reification<Element>> {
        override val typeKey: SuppliedType.Regular<Reification<Element>> =
            SuppliedType.Regular(
                kClass = Reification::class,
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
public fun <Element> loadReificationFor(elementType: SuppliedType<Element>): Reification<Element> = load(Reification.Key(elementType))
context(_: KoneContextRegistry)
public fun <Element> loadReificationForOrNull(elementType: SuppliedType<Element>): Reification<Element>? = loadOrNull(Reification.Key(elementType))
context(_: KoneContextRegistry)
public fun <Element> loadReificationForOrDefault(elementType: SuppliedType<Element>, default: Reification<Element>): Reification<Element> = loadOrDefault(Reification.Key(elementType), default)
context(_: KoneContextRegistry)
public inline fun <Element> loadReificationForOrElse(elementType: SuppliedType<Element>, block: () -> Reification<Element>): Reification<Element> = loadOrElse(Reification.Key(elementType), block)

public inline fun <reified Element> KoneContextRegistryBuilder.installReificationFor(elementType: SuppliedType<Element>) {
    contextsBuilder[Reification.Key(elementType)] = Reification()
}

public class ReificationException(message: String) : RuntimeException(message)

public fun reificationException(message: String = "Value can not be reified"): Nothing = throw ReificationException(message)

context(reification: Reification<Element>)
public fun <Element> reifyMaybe(element: Any?): Maybe<Element> = reification.reifyMaybe(element)
context(reification: Reification<Element>)
public fun <Element> reifyOrNull(element: Any?): Element? = reification.reifyOrNull(element)
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
public inline fun <reified Element> Reification(): Reification<Element> =
    object : Reification<Element> {
        override fun contains(element: Any?): Boolean = element is Element
        override fun reifyMaybe(element: Any?): Maybe<Element> = if (element is Element) Some(element) else None
        override fun reifyOrNull(element: Any?): Element? = element as? Element
        override fun reify(element: Any?): Element = if (element is Element) element else reificationException()
    }

public inline fun <reified Element, Result> Reification(block: context(Reification<Element>) () -> Result): Result =
    block(Reification())