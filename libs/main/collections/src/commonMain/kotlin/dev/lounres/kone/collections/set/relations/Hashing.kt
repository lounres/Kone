/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.set.relations

import dev.lounres.kone.collections.iterator.getAndMoveNext
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash


internal open class KoneSetHashing<Element>(open val elementHashing: Hashing<Element>) : Hashing<KoneSet<out Element>> {
    override fun KoneSet<out Element>.hash(): Int {
        val thisIterator = this.iterator()
        var hash = 0
        while (thisIterator.hasNext()) elementHashing {
            hash += thisIterator.getAndMoveNext().hash()
        }
        return hash
    }
}

public fun <Element> KoneSet.Companion.hashing(elementContext: Hashing<Element>): Hashing<KoneSet<out Element>> =
    KoneSetHashing(elementContext)