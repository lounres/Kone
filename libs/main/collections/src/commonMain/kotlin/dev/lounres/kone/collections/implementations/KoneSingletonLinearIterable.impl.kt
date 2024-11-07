/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneLinearIterable
import dev.lounres.kone.collections.KoneLinearIterator


internal class KoneSingletonLinearIterable<Element>(val singleElement: Element): KoneLinearIterable<Element> {
    override fun iterator(): KoneLinearIterator<Element> = KoneSingletonLinearIterator(singleElement)
}