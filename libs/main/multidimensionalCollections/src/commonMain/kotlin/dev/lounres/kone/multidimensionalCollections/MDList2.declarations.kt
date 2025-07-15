/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray
import kotlinx.serialization.Serializable


//@Serializable(with = MDList2Serializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
public interface MDList2<out E>: MDList<E> {
    public val rowNumber: UInt
    public val columnNumber: UInt
    override val shape: MDShape get() = MDShape(rowNumber, columnNumber)

    public operator fun get(rowIndex: UInt, columnIndex: UInt): E
    override operator fun get(index: KoneUIntArray): E {
        if (
            index.size != 2u ||
            index[0u] >= rowNumber ||
            index[1u] >= columnNumber
        ) indexOutOfShapeException(index = index, shape = shape)
        return get(index[0u], index[1u])
    }
}

//@Serializable(with = SettableMDList2Serializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
public interface SettableMDList2<E>: SettableMDList<E>, MDList2<E> {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, element: E)
    override fun set(index: KoneUIntArray, element: E) {
        if (
            index.size != 2u ||
            index[0u] >= rowNumber ||
            index[1u] >= columnNumber
        ) indexOutOfShapeException(shape = shape, index = index)
        set(index[0u], index[1u], element)
    }
}