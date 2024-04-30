/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.option.None
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.Some


public fun KoneCollection<*>.isEmpty(): Boolean = size == 0u
public fun KoneCollection<*>.isNotEmpty(): Boolean = !isEmpty()

public fun <E> KoneCollection<E>.containsAll(elements: KoneIterableCollection<E>): Boolean {
    for (e in elements) if (e !in this) return false
    return true
}

public inline fun <E> KoneRemovableCollection<E>.retainAllThat(crossinline predicate: (E) -> Boolean) {
    removeAllThat { !predicate(it) }
}

public fun <E> KoneRemovableCollection<E>.removeAllFrom(elements: KoneCollection<E>) {
    removeAllThat { it in elements }
}

public fun <E> KoneRemovableCollection<E>.retainAllFrom(elements: KoneCollection<E>) {
    retainAllThat { it in elements }
}

public fun <E> KoneExtendableCollection<E>.addAll(vararg elements: E) {
    addAllFrom(KoneArray(elements))
}

public fun <E> KoneDequeue<out E>.getFirstOrNull(): E? = if (size > 0u) getFirst() else null

public fun <E> KoneDequeue<out E>.getLastOrNull(): E? = if (size > 0u) getLast() else null

public fun <E> KoneDequeue<out E>.getFirstMaybe(): Option<E> = if (size > 0u) Some(getFirst()) else None

public fun <E> KoneDequeue<out E>.getLastMaybe(): Option<E> = if (size > 0u) Some(getLast()) else None

public fun <E> KoneDequeue<out E>.getFirstOrDefault(default: E): E = if (size > 0u) getFirst() else default

public fun <E> KoneDequeue<out E>.getLastOrDefault(default: E): E = if (size > 0u) getLast() else default

public inline fun <E> KoneDequeue<out E>.getFirstOrElse(default: () -> E): E = if (size > 0u) getFirst() else default()

public inline fun <E> KoneDequeue<out E>.getLastOrElse(default: () -> E): E = if (size > 0u) getLast() else default()

public fun <E> KoneDequeue<out E>.popFirst(): E = getFirst().also { removeFirst() }

public fun <E> KoneDequeue<out E>.popLast(): E = getLast().also { removeLast() }

public fun <E> KoneDequeue<out E>.popFirstOrNull(): E? = if (size > 0u) getFirst().also { removeFirst() } else null

public fun <E> KoneDequeue<out E>.popLastOrNull(): E? = if (size > 0u) getLast().also { removeLast() } else null

public fun <E> KoneDequeue<out E>.popFirstMaybe(): Option<E> = if (size > 0u) Some(getFirst()).also { removeFirst() } else None

public fun <E> KoneDequeue<out E>.popLastMaybe(): Option<E> = if (size > 0u) Some(getLast()).also { removeLast() } else None

public fun <E> KoneDequeue<out E>.popFirstOrDefault(default: E): E = if (size > 0u) getFirst().also { removeFirst() } else default

public fun <E> KoneDequeue<out E>.popLastOrDefault(default: E): E = if (size > 0u) getLast().also { removeLast() } else default

public inline fun <E> KoneDequeue<out E>.popFirstOrElse(default: () -> E): E = if (size > 0u) getFirst().also { removeFirst() } else default()

public inline fun <E> KoneDequeue<out E>.popLastOrElse(default: () -> E): E = if (size > 0u) getLast().also { removeLast() } else default()

public fun <E> KoneDequeue<E>.replaceFirst(element: E) {
    removeFirst()
    addFirst(element)
}

public fun <E> KoneDequeue<E>.replaceLast(element: E) {
    removeLast()
    addLast(element)
}