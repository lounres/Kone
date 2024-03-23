/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common

import dev.lounres.kone.collections.next


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

public fun <E> KoneList<E>.indexOf(element: E): UInt = indexThat { _, collectionElement -> collectionElement == element }

public fun <E> KoneList<E>.lastIndexOf(element: E): UInt = lastIndexThat { _, collectionElement -> collectionElement == element }