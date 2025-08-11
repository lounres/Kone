/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.relations

import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.neq
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDSizeStrides
import dev.lounres.kone.multidimensionalCollections.contentEquals
import dev.lounres.kone.multidimensionalCollections.utils.fold
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.defaultHashing


internal class MDListEquality<E>(private val elementEquality: Equality<E>) : Equality<MDList<E>> {
    override fun MDList<E>.equalsTo(other: MDList<E>): Boolean {
        if (this === other) return true
        if (!(this.size contentEquals other.size)) return false
        
        for (index in MDSizeStrides(this.size)) if (elementEquality { this[index] neq other[index] }) return false
        
        return true
    }
}

public fun <E> MDList.Companion.equality(elementEquality: Equality<E> = defaultEquality()): Equality<MDList<E>> = MDListEquality(elementEquality)

internal class MDListHashing<E>(private val elementHashing: Hashing<E>) : Hashing<MDList<E>> {
    override fun MDList<E>.hash(): Int = this.fold(0) { acc, element -> acc xor elementHashing { element.hash() } } // TODO: Maybe replace with `foldIndexed` with more complex hashing
}

public fun <E> MDList.Companion.hashing(elementHashing: Hashing<E> = defaultHashing()): Hashing<MDList<E>> = MDListHashing(elementHashing)