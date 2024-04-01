/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.collections.KoneUIntArray
import dev.lounres.kone.collections.contentEquals
import dev.lounres.kone.collections.utils.fold
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.context.invoke
import dev.lounres.kone.multidimensionalCollections.Shape
import dev.lounres.kone.multidimensionalCollections.ShapeStrides
import dev.lounres.kone.multidimensionalCollections.experiment1.utils.fold


public interface MDList<out E> {
    public val shape: Shape
    public val dimension: UInt get() = shape.size
    public operator fun get(index: KoneUIntArray): E

    public val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
}

public operator fun <E> MDList<E>.get(vararg index: UInt): E = get(KoneUIntArray(index))

public interface SettableMDList<E>: MDList<E> {
    public operator fun set(index: KoneUIntArray, element: E)
}

public operator fun <E> SettableMDList<E>.set(vararg index: UInt, element: E) {
    set(KoneUIntArray(index), element)
}

internal class MDListEquality<E>(private val elementEquality: Equality<E>) : Equality<MDList<E>> {
    override fun MDList<E>.equalsTo(other: MDList<E>): Boolean {
        if (this === other) return true
        if (!(this.shape contentEquals other.shape)) return false

        for (index in ShapeStrides(this.shape)) if (elementEquality { this[index] neq other[index] }) return false

        return true
    }
}

public fun <E> mdListEquality(elementEquality: Equality<E>): Equality<MDList<E>> = MDListEquality(elementEquality)

internal class MDListHashing<E>(private val elementHashing: Hashing<E>) : Hashing<MDList<E>> {
    override fun MDList<E>.equalsTo(other: MDList<E>): Boolean {
        if (this === other) return true
        if (!(this.shape contentEquals other.shape)) return false

        for (index in ShapeStrides(this.shape)) if (elementHashing { this[index] neq other[index] }) return false

        return true
    }

    override fun MDList<E>.hash(): Int = this.fold(0) { acc, element -> acc xor elementHashing { element.hash() } } // Maybe replace with `foldIndexed` with more complex hashing
}

public fun <E> mdListHashing(elementHashing: Hashing<E>): Hashing<MDList<E>> = MDListHashing(elementHashing)