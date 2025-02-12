/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap.implementations

import dev.lounres.kone.comparison.Order


public fun <Element, Priority> KoneGCBinaryMinimumHeap(priorityContext: Order<Priority>): KoneGCBinaryMinimumHeap<Element, Priority> =
    KoneGCBinaryMinimumHeap(
        priorityContext = priorityContext,
        rootHolder = null,
        lastHolder = null,
    )