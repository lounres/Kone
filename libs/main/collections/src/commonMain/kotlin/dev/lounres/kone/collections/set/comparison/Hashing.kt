/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.set.comparison

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.collections.set.toKoneSet
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.hash
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal open class KoneSetHashing<Element>(open val elementContext: Hashing<Element>) : Hashing<KoneSet<out Element>> {
    override fun KoneSet<out Element>.equalsTo(other: KoneSet<out Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisCopied = this.toKoneSet(elementContext)
        if (thisCopied.size != this.size) return false
        val otherCopied = other.toKoneSet(elementContext)
        if (otherCopied.size != other.size) return false
        for (element in thisCopied) if (elementContext { element !in otherCopied }) return false

        return true
    }

    override fun KoneSet<out Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) elementContext {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

internal class KoneSetReifiedHashing<Element>(
    override val elementContext: ReifiedHashing<Element>
) : KoneSetHashing<Element>(
    elementContext = elementContext
), ReifiedHashing<KoneSet<out Element>> {
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

public fun <Element> koneSetHashing(elementContext: Hashing<Element>): Hashing<KoneSet<out Element>> =
    KoneSetHashing(elementContext)

public fun <Element> koneSetReifiedHashing(elementContext: ReifiedHashing<Element>): ReifiedHashing<KoneSet<out Element>> =
    KoneSetReifiedHashing(elementContext)