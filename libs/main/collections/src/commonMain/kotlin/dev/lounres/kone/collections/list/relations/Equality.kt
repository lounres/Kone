/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.relations

import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.defaultFor
import dev.lounres.kone.relations.neq


internal class KoneListEquality<Element>(val elementEquality: Equality<Element>) : Equality<KoneList<Element>> {
    override fun KoneList<Element>.equalsTo(other: KoneList<Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (elementEquality { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }
}

/**
 * Returns an [Equality] instance for [KoneList] based on the given [elementEquality].
 *
 * @param Element The element type.
 * @param elementEquality The equality strategy for elements.
 * @return An equality for lists.
 */
public fun <Element> KoneList.Companion.equality(elementEquality: Equality<Element> = Equality.defaultFor()): Equality<KoneList<Element>> =
    KoneListEquality(elementEquality)