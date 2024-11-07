/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneIterator


internal open class EmptyKoneIterableTemplate<Element> : KoneIterable<Element> {
    override fun iterator(): KoneIterator<Element> = EmptyKoneIterator
}

internal object EmptyKoneIterable: EmptyKoneIterableTemplate<Nothing>()