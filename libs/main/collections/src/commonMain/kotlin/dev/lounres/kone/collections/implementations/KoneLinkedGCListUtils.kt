/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneLinkedGCList(): KoneLinkedGCList<E, Equality<E>> =
    KoneLinkedGCList(elementContext = defaultEquality())

public fun <E, EC: Equality<E>> KoneLinkedGCList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneLinkedGCList<E, EC> =
    KoneLinkedGCList(elementContext).apply {
        for (index in 0u ..< size) add(initializer(index))
    }

public fun <E> KoneLinkedGCList(size: UInt, initializer: (index: UInt) -> E): KoneLinkedGCList<E, Equality<E>> =
    KoneLinkedGCList<E, _>(defaultEquality()).apply {
        for (index in 0u ..< size) add(initializer(index))
    }