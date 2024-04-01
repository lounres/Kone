/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableArray
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public inline fun <E, EC: Equality<E>> KoneSettableArrayList(size: UInt, elementContext: EC, initializer: (index: UInt) -> E): KoneSettableArrayList<E, EC> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), elementContext = elementContext)

public inline fun <E> KoneSettableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneSettableArrayList<E, Equality<E>> =
    KoneSettableArrayList(KoneMutableArray(size, initializer), elementContext = defaultEquality())