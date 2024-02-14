/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.implementations

import dev.lounres.kone.collections.standard.KoneMutableArray


public fun <E> KoneSettableArrayList(size: UInt, initializer: (index: UInt) -> E): KoneSettableArrayList<E> =
    KoneSettableArrayList(KoneMutableArray(size, initializer))