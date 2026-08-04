/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.iterable.empty

import dev.lounres.kone.collections.iterable.KoneLinearIterable
import dev.lounres.kone.collections.iterator.KoneLinearIterator
import dev.lounres.kone.collections.iterator.empty.KoneEmptySettableLinearIterator


internal open class KoneEmptySettableLinearIterableTemplate<out Element> : KoneLinearIterable<Element> {
    override val size: UInt get() = 0u
    override fun iterator(): KoneLinearIterator<Element> = KoneEmptySettableLinearIterator
}

internal object KoneEmptySettableLinearIterable: KoneEmptySettableLinearIterableTemplate<Nothing>()