/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.collections.implementations.KoneSettableArrayList
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.defaultEquality


// TODO: Apply where it is necessary.
internal fun <E> KoneIterableList<E>.optimizeReadOnlyList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList()
        else -> {
            val iterator = this.iterator()
            KoneSettableArrayList(this.size, context = context) { iterator.getAndMoveNext() }
        }
    }
internal fun <E> KoneList<E>.optimizeReadOnlyList(context: Equality<E> = defaultEquality()): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList()
        else -> KoneSettableArrayList(this.size, context = context) { this[it] }
    }