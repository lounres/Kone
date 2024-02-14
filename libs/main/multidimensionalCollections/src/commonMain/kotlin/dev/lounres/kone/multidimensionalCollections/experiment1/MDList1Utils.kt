/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1

import dev.lounres.kone.multidimensionalCollections.experiment1.implementations.ArrayMDList1


public fun <E> MDList1(vararg elements: E): MDList1<E> =
    ArrayMDList1(elements.size.toUInt()) { index -> elements[index.toInt()] }
public fun <E> MDList1(size: UInt, initializer: (coefficient: UInt) -> E): MDList1<E> =
    ArrayMDList1(size, initializer)

public val MDList1<*>.indices: UIntRange get() = 0u ..< size