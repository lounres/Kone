/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.emptyKoneList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.implementations.KoneSingletonNoddedList


// TODO: Apply where it is necessary.
@PublishedApi
internal fun <E> KoneList<E>.toOptimizedList(): KoneList<E> =
    when (size) {
        0u -> emptyKoneList()
        1u -> KoneSingletonNoddedList(this.first())
        else -> {
            val iterator = this.iterator()
            KoneSettableArrayList(this.size) { iterator.getAndMoveNext() }
        }
    }