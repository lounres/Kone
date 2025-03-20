/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.relations

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.context
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.neq


internal open class KoneListEquality<Element>(open val elementEquality: Equality<Element>) : Equality<KoneList<Element>> {
    override fun KoneList<Element>.equalsTo(other: KoneList<Element>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false

        val thisIterator = this.iterator()
        val otherIterator = other.iterator()
        while (thisIterator.hasNext()) {
            if (context(elementEquality) { thisIterator.getAndMoveNext() neq otherIterator.getAndMoveNext() }) return false
        }

        return true
    }
}

public fun <Element> koneListEquality(elementEquality: Equality<Element>): Equality<KoneList<Element>> =
    KoneListEquality(elementEquality)