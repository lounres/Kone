/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections

import dev.lounres.kone.collections.array.KoneUIntArray


public interface MDList1<out E>: MDList<E> {
    public override val size: UInt
    override val shape: MDShape get() = MDShape(size)
    public operator fun get(index: UInt): E
    override fun get(index: KoneUIntArray): E {
        if (index.size != 1u || index[0u] >= shape[0u]) indexOutOfShapeException(shape = shape, index = index)
        return get(index[0u])
    }
}

public interface SettableMDList1<E>: SettableMDList<E>, MDList1<E> {
    public operator fun set(index: UInt, element: E)
    override fun set(index: KoneUIntArray, element: E) {
        if (index.size != 1u || index[0u] >= size) indexOutOfShapeException(shape = shape, index = index)
        set(index[0u], element)
    }
}