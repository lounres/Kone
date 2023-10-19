/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray


public fun <E> KoneGrowableLinkedList(): KoneGrowableLinkedList<E> = KoneGrowableLinkedList(size = 0u)

public fun <E> KoneGrowableLinkedList(size: UInt, initializer: (index: UInt) -> E): KoneGrowableLinkedList<E> {
    val sizeUpperBound = powerOf2GreaterOrEqualTo(size)
    return KoneGrowableLinkedList(
        size = size,
        sizeUpperBound = sizeUpperBound,
        data = KoneMutableArray(sizeUpperBound) { if (it < size) initializer(it) else null }
    )
}