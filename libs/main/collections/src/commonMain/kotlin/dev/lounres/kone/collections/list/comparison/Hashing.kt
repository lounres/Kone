/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.comparison

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.collections.utils.any
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal open class KoneListHashing<Element>(open val elementContext: Hashing<Element>) : Hashing<KoneList<Element>> {
    override fun KoneList<Element>.equalsTo(other: KoneList<Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        if (this.hash() != other.hash()) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }

    override fun KoneList<Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 1
        while (thisIterator.hasNext()) elementContext {
            hash = 31 * hash + thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

internal class KoneListReifiedHashing<Element>(
    override val elementContext: ReifiedHashing<Element>
) : KoneListHashing<Element>(elementContext), ReifiedHashing<KoneList<Element>> {
    override fun contains(element: Any?): Boolean = element is KoneList<*> && element.all { it in elementContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneList<Element>> =
        when {
            element !is KoneList<*> -> None
            element.any { it !in elementContext } -> None
            else -> Some(elementContext as KoneList<Element>)
        }
    override fun reifyOrNull(element: Any?): KoneList<Element>? =
        when {
            element !is KoneList<*> -> null
            element.any { it !in elementContext } -> null
            else -> elementContext as KoneList<Element>
        }
    override fun reify(element: Any?): KoneList<Element> =
        when {
            element !is KoneList<*> -> reificationException()
            element.any { it !in elementContext } -> reificationException()
            else -> elementContext as KoneList<Element>
        }
}

public fun <Element> koneListHashing(elementContext: Hashing<Element>): Hashing<KoneList<Element>> =
    KoneListHashing(elementContext)

public fun <Element> koneListReifiedHashing(elementContext: ReifiedHashing<Element>): ReifiedHashing<KoneList<Element>> =
    KoneListReifiedHashing(elementContext)