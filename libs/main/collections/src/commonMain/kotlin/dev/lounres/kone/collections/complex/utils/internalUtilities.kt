/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.utils

import dev.lounres.kone.collections.complex.KoneIterableList
import dev.lounres.kone.collections.complex.KoneList
import dev.lounres.kone.collections.complex.implementations.KoneSettableArrayList
import dev.lounres.kone.collections.getAndMoveNext
import dev.lounres.kone.comparison.Equality


// TODO: Apply where it is necessary.
internal fun <E> KoneIterableList<E>.optimizeReadOnlyList(context: Equality<E> = this.context): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList(context = context)
        else -> {
            val iterator = this.iterator()
            KoneSettableArrayList(this.size, context = context) { iterator.getAndMoveNext() }
        }
    }
internal fun <E> KoneList<E>.optimizeReadOnlyList(context: Equality<E> = this.context): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList(context = context)
        else -> KoneSettableArrayList(this.size, context = context) { this[it] }
    }