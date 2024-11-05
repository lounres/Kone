/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Order


public fun <E, P, PC: Order<P>> BinaryGCMinimumHeap(priorityContext: PC): BinaryGCMinimumHeap<E, P, PC> =
    BinaryGCMinimumHeap(
        priorityContext = priorityContext,
        rootHolder = null,
        lastHolder = null,
    )