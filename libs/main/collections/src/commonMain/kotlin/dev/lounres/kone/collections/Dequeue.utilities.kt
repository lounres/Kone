/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public fun KoneDequeue<*>.isEmpty(): Boolean = size == 0u
public fun KoneDequeue<*>.isNotEmpty(): Boolean = !isEmpty()

public fun <E> KoneDequeue<out E>.popFirst(): E = getFirst().also { removeFirst() }
public fun <E> KoneDequeue<out E>.popLast(): E = getLast().also { removeLast() }