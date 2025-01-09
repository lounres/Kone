/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.list.emptyKoneList
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableNoddedList


// TODO: Apply where it is necessary.
@PublishedApi
internal fun <E> KoneList<E>.toOptimizedList(): KoneList<E> =
    when (size) {
        0u -> emptyKoneList()
        1u -> KoneSingletonSettableNoddedList(this.first())
        else -> {
            val iterator = this.iterator()
            KoneArraySettableList(this.size) { iterator.getAndMoveNext() }
        }
    }