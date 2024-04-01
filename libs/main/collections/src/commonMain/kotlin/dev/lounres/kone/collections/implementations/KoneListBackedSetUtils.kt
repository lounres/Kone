/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


public inline fun <E, EC: Equality<E>> KoneListBackedSet(elementContext: EC, backingListFabric: (EC) -> KoneIterableList<E>): KoneListBackedSet<E, EC> =
    KoneListBackedSet(elementContext, backingListFabric(elementContext))

public inline fun <E> KoneListBackedSet(backingListFabric: (Equality<E>) -> KoneIterableList<E>): KoneListBackedSet<E, Equality<E>> =
    KoneListBackedSet(defaultEquality(), backingListFabric(defaultEquality()))