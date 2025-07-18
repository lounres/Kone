/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.list.relations

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.defaultHashing


internal open class KoneListHashing<Element>(open val elementHashing: Hashing<Element>) : Hashing<KoneList<Element>> {
    override fun KoneList<Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 1
        while (thisIterator.hasNext()) elementHashing {
            hash = 31 * hash + thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <Element> KoneList.Companion.hashing(elementHashing: Hashing<Element> = defaultHashing()): Hashing<KoneList<Element>> =
    KoneListHashing(elementHashing)