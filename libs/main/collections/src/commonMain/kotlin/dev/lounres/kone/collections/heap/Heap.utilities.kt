/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.heap


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