/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.complex.KoneMutableArray
import dev.lounres.kone.comparison.Equality


public fun <E> KoneSettableArrayList(size: UInt, context: Equality<E>, initializer: (index: UInt) -> E): KoneSettableArrayList<E> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), context = context)

context(Equality<E>)
public fun <E> KoneSettableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneSettableArrayList<E> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), context = this@Equality)