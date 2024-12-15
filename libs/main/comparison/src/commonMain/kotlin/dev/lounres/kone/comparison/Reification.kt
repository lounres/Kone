/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.comparison

import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


public interface Reification<out Element> {
    public operator fun contains(element: Any?): Boolean
    public fun reifyMaybe(element: Any?): Maybe<Element>
    public fun reifyOrNull(element: Any?): Element?
    public fun reify(element: Any?): Element
}

public class ReificationException(message: String) : RuntimeException(message)

public fun reificationException(message: String = "Value can not be reified"): Nothing = throw ReificationException(message)

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