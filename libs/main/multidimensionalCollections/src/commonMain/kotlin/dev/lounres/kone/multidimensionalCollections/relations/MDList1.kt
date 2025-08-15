/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.relations

import dev.lounres.kone.contexts.invoke
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.hash
import dev.lounres.kone.relations.neq
import dev.lounres.kone.multidimensionalCollections.MDList1
import dev.lounres.kone.multidimensionalCollections.utils.fold
import dev.lounres.kone.relations.defaultFor


internal class MDList1Equality<E>(private val elementEquality: Equality<E>) : Equality<MDList1<E>> {
    override fun MDList1<E>.equalsTo(other: MDList1<E>): Boolean {
        if (this === other) return true
        if (this.size != other.size) return false
        
        for (index in 0u .. this.size[0u]) if (elementEquality { this[index] neq other[index] }) return false
        
        return true
    }
}

public fun <E> MDList1.Companion.equality(elementEquality: Equality<E> = Equality.defaultFor()): Equality<MDList1<E>> = MDList1Equality(elementEquality)

internal class MDList1Hashing<E>(private val elementHashing: Hashing<E>) : Hashing<MDList1<E>> {
    override fun MDList1<E>.hash(): Int = this.fold(0) { acc, element -> acc xor elementHashing { element.hash() } } // TODO: Maybe replace with `foldIndexed` with more complex hashing
}

public fun <E> MDList1.Companion.hashing(elementHashing: Hashing<E> = Hashing.defaultFor()): Hashing<MDList1<E>> = MDList1Hashing(elementHashing)