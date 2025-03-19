/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.deque


public fun KoneDeque<*>.isEmpty(): Boolean = size == 0u
public fun KoneDeque<*>.isNotEmpty(): Boolean = !isEmpty()

public fun <Element> KoneDeque<out Element>.popFirst(): Element = getFirst().also { removeFirst() }
public fun <Element> KoneDeque<out Element>.popLast(): Element = getLast().also { removeLast() }