/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.KoneIterableList


// TODO: Maybe it should be regenerated as a KoneSettableArrayList.
internal fun <E> KoneIterableList<E>.optimizeReadOnlyList(): KoneIterableList<E> =
    when (size) {
        0u -> emptyKoneIterableList()
        else -> this
    }