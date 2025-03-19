/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.comparison

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.hash
import dev.lounres.kone.context


internal open class KoneListHashing<Element>(open val elementHashing: Hashing<Element>) : Hashing<KoneList<Element>> {
    override fun KoneList<Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 1
        while (thisIterator.hasNext()) context(elementHashing) {
            hash = 31 * hash + thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <Element> koneListHashing(elementHashing: Hashing<Element>): Hashing<KoneList<Element>> =
    KoneListHashing(elementHashing)