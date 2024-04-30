/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing


public fun <E, EC: Hashing<E>> KoneResizableHashSet(elementContext: EC): KoneResizableHashSet<E, EC> =
    KoneResizableHashSet(size = 0u, elementContext = elementContext)

public fun <E> KoneResizableHashSet(): KoneResizableHashSet<E, Hashing<E>> =
    KoneResizableHashSet(size = 0u, elementContext = defaultHashing())