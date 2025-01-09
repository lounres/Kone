/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.dequeue


public fun KoneDequeue<*>.isEmpty(): Boolean = size == 0u
public fun KoneDequeue<*>.isNotEmpty(): Boolean = !isEmpty()

public fun <Element> KoneDequeue<out Element>.popFirst(): Element = getFirst().also { removeFirst() }
public fun <Element> KoneDequeue<out Element>.popLast(): Element = getLast().also { removeLast() }