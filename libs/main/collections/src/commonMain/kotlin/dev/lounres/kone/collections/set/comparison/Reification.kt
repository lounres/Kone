/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.set.comparison

import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal class KoneSetReification<Element>(
    val elementReification: Reification<Element>,
) :  Reification<KoneSet<out Element>> {
    override fun contains(element: Any?): Boolean = element is KoneSet<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneSet<out Element>> =
        when {
            element !is KoneSet<*> -> None
            element.any { it !in elementReification } -> None
            else -> Some(element as KoneSet<out Element>)
        }
    override fun reifyOrNull(element: Any?): KoneSet<out Element>? =
        when {
            element !is KoneSet<*> -> null
            element.any { it !in elementReification } -> null
            else -> element as KoneSet<out Element>
        }
    override fun reify(element: Any?): KoneSet<out Element> =
        when {
            element !is KoneSet<*> -> reificationException()
            element.any { it !in elementReification } -> reificationException()
            else -> element as KoneSet<out Element>
        }
}

public fun <Element> koneSetReification(elementReification: Reification<Element>): Reification<KoneSet<out Element>> =
    KoneSetReification(elementReification)