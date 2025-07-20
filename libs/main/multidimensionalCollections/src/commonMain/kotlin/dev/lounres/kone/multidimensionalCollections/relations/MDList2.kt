/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.relations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.neq
import dev.lounres.kone.multidimensionalCollections.MDList2
import dev.lounres.kone.multidimensionalCollections.utils.fold
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.defaultHashing


internal class MDList2Equality<E>(private val elementEquality: Equality<E>) : Equality<MDList2<E>> {
    override fun MDList2<E>.equalsTo(other: MDList2<E>): Boolean {
        if (this === other) return true
        if (this.rowNumber != other.rowNumber || this.columnNumber != other.columnNumber) return false
        
        for (rowIndex in 0u .. this.rowNumber) for (columnIndex in 0u .. this.columnNumber)
            if (context(elementEquality) { this[rowIndex, columnIndex] neq other[rowIndex, columnIndex] }) return false
        
        return true
    }
}

public fun <E> MDList2.Companion.equality(elementEquality: Equality<E> = defaultEquality()): Equality<MDList2<E>> = MDList2Equality(elementEquality)

internal class MDList2Hashing<E>(private val elementHashing: Hashing<E>) : Hashing<MDList2<E>> {
    override fun MDList2<E>.hash(): Int = this.fold(0) { acc, element -> acc xor context(elementHashing) { element.hash() } } // TODO: Maybe replace with `foldIndexed` with more complex hashing
}

public fun <E> MDList2.Companion.hashing(elementHashing: Hashing<E> = defaultHashing()): Hashing<MDList2<E>> = MDList2Hashing(elementHashing)