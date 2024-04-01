/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.comparison.Equality


public inline fun <E, EC: Equality<E>> KoneMutableListBackedSet(elementContext: EC, backingListFabric: (EC) -> KoneMutableIterableList<E>): KoneMutableListBackedSet<E, EC> =
    KoneMutableListBackedSet(elementContext, backingListFabric(elementContext))

public fun <E, EC: Equality<E>> KoneMutableListBackedSet(elementContext: EC): KoneMutableListBackedSet<E, EC> =
    KoneMutableListBackedSet(elementContext, KoneResizableLinkedArrayList(elementContext = elementContext))