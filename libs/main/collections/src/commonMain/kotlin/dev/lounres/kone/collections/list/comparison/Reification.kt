/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.comparison

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.reificationException
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal class KoneListReification<Element>(
    val elementReification: Reification<Element>
) :  Reification<KoneList<Element>> {
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

public fun <Element> koneListReification(elementReification: Reification<Element>): Reification<KoneList<Element>> =
    KoneListReification(elementReification)