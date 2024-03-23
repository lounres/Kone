/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.contextual.KoneContextualIterableList
import dev.lounres.kone.comparison.Equality


// TODO: Maybe it should be regenerated as a KoneSettableArrayList.
// TODO: Apply where it is necessary.
internal fun <E, EE: Equality<E>> KoneContextualIterableList<E, EE>.optimizeReadOnlyList(): KoneContextualIterableList<E, EE> =
    when (size) {
        0u -> emptyKoneContextualIterableList()
        else -> this
    }