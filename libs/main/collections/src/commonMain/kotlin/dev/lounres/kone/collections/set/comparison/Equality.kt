/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.set.comparison

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.toKoneSet
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.*
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal open class KoneSetEquality<Element>(open val elementContext: Equality<Element>) : Equality<KoneSet<out Element>> {
    override fun KoneSet<out Element>.equalsTo(other: KoneSet<out Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        
        val thisCopied = this.toKoneSet(elementContext)
        if (thisCopied.size != this.size) return false
        val otherCopied = other.toKoneSet(elementContext)
        if (otherCopied.size != other.size) return false
        for (element in thisCopied) if (elementContext { element !in otherCopied }) return false
        
        return true
    }
}

internal class KoneSetReifiedEquality<Element>(
    override val elementContext: ReifiedEquality<Element>
) : KoneSetEquality<Element>(
    elementContext = elementContext,
), ReifiedEquality<KoneSet<out Element>> {
    override fun contains(element: Any?): Boolean = element is KoneSet<*> && element.all { it in elementContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneSet<out Element>> =
        when {
            element !is KoneSet<*> -> None
            element.any { it !in elementContext } -> None
            else -> Some(elementContext as KoneSet<out Element>)
        }
    override fun reifyOrNull(element: Any?): KoneSet<out Element>? =
        when {
            element !is KoneSet<*> -> null
            element.any { it !in elementContext } -> null
            else -> elementContext as KoneSet<out Element>
        }
    override fun reify(element: Any?): KoneSet<out Element> =
        when {
            element !is KoneSet<*> -> reificationException()
            element.any { it !in elementContext } -> reificationException()
            else -> elementContext as KoneSet<out Element>
        }
}

public fun <Element> koneSetEquality(elementContext: Equality<Element>): Equality<KoneSet<out Element>> =
    if (elementContext is Hashing<Element>) KoneSetHashing(elementContext)
    else KoneSetEquality(elementContext)

public fun <Element> koneReifiedSetEquality(elementContext: ReifiedEquality<Element>): ReifiedEquality<KoneSet<out Element>> =
    if (elementContext is ReifiedHashing<Element>) KoneSetReifiedHashing(elementContext)
    else KoneSetReifiedEquality(elementContext)