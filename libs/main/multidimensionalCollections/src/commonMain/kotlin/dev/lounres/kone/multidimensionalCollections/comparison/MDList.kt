/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.comparison

import dev.lounres.kone.collections.array.contentEquals
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.hash
import dev.lounres.kone.comparison.neq
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.MDList
import dev.lounres.kone.multidimensionalCollections.MDShapeStrides
import dev.lounres.kone.multidimensionalCollections.utils.fold


internal class MDListEquality<E>(private val elementEquality: Equality<E>) : Equality<MDList<E>> {
    override fun MDList<E>.equalsTo(other: MDList<E>): Boolean {
        if (this === other) return true
        if (!(this.shape contentEquals other.shape)) return false
        
        for (index in MDShapeStrides(this.shape)) if (elementEquality { this[index] neq other[index] }) return false
        
        return true
    }
}

public fun <E> mdListEquality(elementEquality: Equality<E>): Equality<MDList<E>> = MDListEquality(elementEquality)

internal class MDListHashing<E>(private val elementHashing: Hashing<E>) : Hashing<MDList<E>> {
    override fun MDList<E>.equalsTo(other: MDList<E>): Boolean {
        if (this === other) return true
        if (!(this.shape contentEquals other.shape)) return false
        
        for (index in MDShapeStrides(this.shape)) if (elementHashing { this[index] neq other[index] }) return false
        
        return true
    }
    
    override fun MDList<E>.hash(): Int = this.fold(0) { acc, element -> acc xor elementHashing { element.hash() } } // Maybe replace with `foldIndexed` with more complex hashing
}

public fun <E> mdListHashing(elementHashing: Hashing<E>): Hashing<MDList<E>> = MDListHashing(elementHashing)