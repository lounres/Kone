/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneSettableLinearIterable
import dev.lounres.kone.collections.KoneSettableLinearIterator


internal class KoneSingletonSettableLinearIterable<Element>(val singleElement: Element): KoneSettableLinearIterable<Element> {
    override val size: UInt get() = 1u
    override fun iterator(): KoneSettableLinearIterator<Element> = KoneSingletonSettableLinearIterator(singleElement)
}