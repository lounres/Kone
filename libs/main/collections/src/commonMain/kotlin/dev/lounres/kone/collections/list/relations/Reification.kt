/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.relations

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.maybe.Maybe
import dev.lounres.kone.maybe.None
import dev.lounres.kone.maybe.Some


internal class KoneListReification<Element>(
    val elementReification: Reification<Element>
) : Reification<KoneList<Element>> {
    override fun contains(element: Any?): Boolean =
        element is KoneList<*> && element.all { it in elementReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneList<Element>> =
        if (element is KoneList<*> && element.all { it in elementReification }) Some(element as KoneList<Element>)
        else None
    override fun reifyOrNull(element: Any?): KoneList<Element>? =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneList<Element>
        else null
    override fun reify(element: Any?): KoneList<Element> =
        if (element is KoneList<*> && element.all { it in elementReification }) element as KoneList<Element>
        else reificationException()
}

/**
 * Returns a [Reification] instance for [KoneList] based on the given [elementReification].
 *
 * @param Element The element type.
 * @param elementReification The reification strategy for elements.
 * @return A reification for lists.
 */
public fun <Element> KoneList.Companion.reification(elementReification: Reification<Element>): Reification<KoneList<Element>> =
    KoneListReification(elementReification)