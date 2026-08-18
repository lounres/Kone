/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap

import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.toKoneReifiedSet
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.absoluteFor
import dev.lounres.kone.relations.defaultFor


/**
 * Checks if the heap is empty.
 */
public fun MinimumHeap<*, *>.isEmpty(): Boolean = size == 0u
/**
 * Checks if the heap is empty.
 */
public fun MaximumHeap<*, *>.isEmpty(): Boolean = size == 0u
/**
 * Checks if the heap is not empty.
 */
public fun MinimumHeap<*, *>.isNotEmpty(): Boolean = !isEmpty()
/**
 * Checks if the heap is not empty.
 */
public fun MaximumHeap<*, *>.isNotEmpty(): Boolean = !isEmpty()
public val <Element, Priority> MinimumHeap<Element, Priority>.nodes: KoneReifiedSet<HeapNode<Element, Priority>>
    get() = nodesView.toKoneReifiedSet(
        elementReification = Reification.defaultFor(),
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )
public val <Element, Priority> MaximumHeap<Element, Priority>.nodes: KoneReifiedSet<HeapNode<Element, Priority>>
    get() = nodesView.toKoneReifiedSet(
        elementReification = Reification.defaultFor(),
        elementEquality = Equality.absoluteFor(),
        elementHashing = Hashing.defaultFor(),
    )