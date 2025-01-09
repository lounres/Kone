/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.comparison

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.comparison.*
import dev.lounres.kone.context.invoke
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal open class KoneListEquality<Element>(open val elementContext: Equality<Element>) : Equality<KoneList<Element>> {
    override fun KoneList<Element>.equalsTo(other: KoneList<Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementContext { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }
}

internal class KoneListReifiedEquality<Element>(
    override val elementContext: ReifiedEquality<Element>
) : KoneListEquality<Element>(
    elementContext = elementContext,
), ReifiedEquality<KoneList<Element>> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementContext }
    override fun reifyMaybe(element: Any?): Maybe<KoneList<Element>> =
        if (element is KoneList<*> && element.all { it in elementContext }) Some(element as KoneList<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneList<Element>? =
        if (element is KoneList<*> && element.all { it in elementContext }) element as KoneList<Element>
        else null
    override fun reify(element: Any?): KoneList<Element> =
        if (element is KoneList<*> && element.all { it in elementContext }) element as KoneList<Element>
        else reificationException()
}

public fun <Element> koneListEquality(elementContext: Equality<Element>): Equality<KoneList<Element>> =
    if (elementContext is Hashing<Element>) KoneListHashing(elementContext)
    else KoneListEquality(elementContext)

public fun <Element> koneListReifiedEquality(elementContext: ReifiedEquality<Element>): ReifiedEquality<KoneList<Element>> =
    if (elementContext is ReifiedHashing<Element>) KoneListReifiedHashing(elementContext)
    else KoneListReifiedEquality(elementContext)