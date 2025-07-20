/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.utils.fold
import kotlinx.serialization.Serializable


// TODO: Maybe `MDShapeIndexer` should be part of the structure?..

//@Serializable(with = MDListSerializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
public interface MDList<out E> {
    public val shape: MDShape
    public val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
    
    public operator fun get(index: KoneUIntArray): E
    
    public companion object
}

//@Serializable(with = SettableMDListSerializer::class) // FIXME: For some reason this does not compile in Kotlin 2.2.20-Beta1
public interface SettableMDList<E>: MDList<E> {
    public operator fun set(index: KoneUIntArray, element: E)
    
    public companion object
}