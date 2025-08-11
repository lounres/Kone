/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray
import kotlinx.serialization.Serializable


// TODO: Maybe `MDSizeIndexer` should be part of the structure?..

@Serializable(with = MDListSerializer::class)
public interface MDList<out E> {
    public val size: MDSize
    
    public operator fun get(index: MDIndex): E
    
    public companion object
}

@Serializable(with = SettableMDListSerializer::class)
public interface SettableMDList<E>: MDList<E> {
    public operator fun set(index: MDIndex, element: E)
    
    public companion object
}