/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.utils

import dev.lounres.kone.collections.common.KoneIterableList
import dev.lounres.kone.collections.common.KoneList
import dev.lounres.kone.collections.common.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.getAndMoveNext


// TODO: Apply where it is necessary.
internal fun <E> KoneIterableList<E>.optimizeReadOnlyList(): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList()
        else -> {
            val iterator = this.iterator()
            KoneSettableArrayList(this.size) { iterator.getAndMoveNext() }
        }
    }
internal fun <E> KoneList<E>.optimizeReadOnlyList(): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList()
        else -> KoneSettableArrayList(this.size) { this[it] }
    }