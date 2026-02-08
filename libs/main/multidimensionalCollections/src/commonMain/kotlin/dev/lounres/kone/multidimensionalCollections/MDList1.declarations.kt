/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import kotlinx.serialization.Serializable


@Serializable(with = MDList1Serializer::class)
public interface MDList1<out E> : MDList<E> {
    override val size: MDSize
    public operator fun get(index: UInt): E
    override fun get(index: MDIndex): E {
        if (index.size != 1u || index[0u] >= size[0u]) mdIndexOutOfSizeException(size = size, index = index)
        return get(index[0u])
    }
    
    public companion object
}

@Serializable(with = SettableMDList1Serializer::class)
public interface SettableMDList1<E> : SettableMDList<E>, MDList1<E> {
    public operator fun set(index: UInt, element: E)
    override fun set(index: MDIndex, element: E) {
        if (index.size != 1u || index[0u] >= size[0u]) mdIndexOutOfSizeException(size = size, index = index)
        set(index[0u], element)
    }
    
    public companion object
}