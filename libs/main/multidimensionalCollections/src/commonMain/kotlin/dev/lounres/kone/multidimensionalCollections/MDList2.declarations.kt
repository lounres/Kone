/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import kotlinx.serialization.Serializable


@Serializable(with = MDList2Serializer::class)
public interface MDList2<out E>: MDList<E> {
    public val rowNumber: UInt
    public val columnNumber: UInt
    override val size: MDSize get() = MDSize.of(rowNumber, columnNumber)

    public operator fun get(rowIndex: UInt, columnIndex: UInt): E
    override operator fun get(index: MDIndex): E {
        if (
            index.size != 2u ||
            index[0u] >= rowNumber ||
            index[1u] >= columnNumber
        ) mdIndexOutOfSizeException(index = index, size = size)
        return get(index[0u], index[1u])
    }
    
    public companion object
}

@Serializable(with = SettableMDList2Serializer::class)
public interface SettableMDList2<E>: SettableMDList<E>, MDList2<E> {
    public operator fun set(rowIndex: UInt, columnIndex: UInt, element: E)
    override fun set(index: MDIndex, element: E) {
        if (
            index.size != 2u ||
            index[0u] >= rowNumber ||
            index[1u] >= columnNumber
        ) mdIndexOutOfSizeException(size = size, index = index)
        set(index[0u], index[1u], element)
    }
    
    public companion object
}