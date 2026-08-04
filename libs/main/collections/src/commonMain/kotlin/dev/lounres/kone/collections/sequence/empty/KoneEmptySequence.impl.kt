/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.sequence.empty

import dev.lounres.kone.collections.iterator.KoneIterator
import dev.lounres.kone.collections.iterator.empty
import dev.lounres.kone.collections.sequence.KoneSequence


internal object KoneEmptySequence : KoneSequence<Nothing> {
    override fun iterator(): KoneIterator<Nothing> = KoneIterator.empty()
}