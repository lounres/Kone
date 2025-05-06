/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.set.relations

import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash
import dev.lounres.kone.context


internal open class KoneSetHashing<Element>(open val elementHashing: Hashing<Element>) : Hashing<KoneSet<out Element>> {
    override fun KoneSet<out Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) context(elementHashing) {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <Element> KoneSet.Companion.hashing(elementContext: Hashing<Element>): Hashing<KoneSet<out Element>> =
    KoneSetHashing(elementContext)