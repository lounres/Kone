/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque.implementations

import dev.lounres.kone.collections.implementations.powerOf2ArraySizeGreaterOrEqualTo


public fun <Element> KoneArrayGrowableCircularDeque(): KoneArrayGrowableCircularDeque<Element> =
    KoneArrayGrowableCircularDeque(size = 0u)

public fun <Element> KoneArrayGrowableCircularDeque(initialCapacity: UInt): KoneArrayGrowableCircularDeque<Element> =
    KoneArrayGrowableCircularDeque(
        size = 0u,
        sizeUpperBound = powerOf2ArraySizeGreaterOrEqualTo(initialCapacity),
    )