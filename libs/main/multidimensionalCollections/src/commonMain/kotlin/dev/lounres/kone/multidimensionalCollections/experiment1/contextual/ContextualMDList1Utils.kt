/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.multidimensionalCollections.experiment1.contextual

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.multidimensionalCollections.experiment1.contextual.implementations.ContextualArrayMDList1


public fun <E> ContextualMDList1(vararg elements: E): ContextualMDList1<E, Equality<E>> =
    ContextualArrayMDList1(elements.size.toUInt()) { index -> elements[index.toInt()] }
public fun <E> ContextualMDList1(size: UInt, initializer: (coefficient: UInt) -> E): ContextualMDList1<E, Equality<E>> =
    ContextualArrayMDList1(size, initializer)

public val ContextualMDList1<*, *>.indices: UIntRange get() = 0u ..< size