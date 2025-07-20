/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.collections.iterables.getAndMoveNext
import dev.lounres.kone.collections.list.KoneNoddedList
import dev.lounres.kone.collections.list.empty
import dev.lounres.kone.collections.list.implementations.KoneArraySettableList
import dev.lounres.kone.collections.list.implementations.KoneArraySettableNoddedList
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableList
import dev.lounres.kone.collections.list.singleton.KoneSingletonSettableNoddedList


// TODO: Apply where it is necessary.
@PublishedApi
internal fun <E> KoneList<E>.toOptimizedList(): KoneList<E> =
    when (size) {
        0u -> KoneList.empty()
        1u -> KoneSingletonSettableList(this.first())
        else -> {
            val iterator = this.iterator()
            KoneArraySettableList(this.size) { iterator.getAndMoveNext() }
        }
    }

@PublishedApi
internal fun <E> KoneList<E>.toOptimizedNoddedList(): KoneNoddedList<E> =
    when (size) {
        0u -> KoneNoddedList.empty()
        1u -> KoneSingletonSettableNoddedList(this.first())
        else -> {
            val iterator = this.iterator()
            KoneArraySettableNoddedList(this.size) { iterator.getAndMoveNext() }
        }
    }