/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public fun <E> KoneSettableArrayList(size: UInt, context: Equality<E> = defaultEquality(), initializer: (index: UInt) -> E): KoneSettableArrayList<E> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), context = context)