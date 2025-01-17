/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray
import dev.lounres.kone.collections.utils.fold


public interface MDList<out E> {
    public val shape: MDShape
    public val size: UInt get() = shape.fold(1u) { acc, dim -> acc * dim }
    
    public operator fun get(index: KoneUIntArray): E
}

public interface SettableMDList<E>: MDList<E> {
    public operator fun set(index: KoneUIntArray, element: E)
}