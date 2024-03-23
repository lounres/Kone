/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual

import dev.lounres.kone.collections.next
import dev.lounres.kone.comparison.Equality


public fun KoneContextualCollection<*, *>.isEmpty(): Boolean = size == 0u
public fun KoneContextualCollection<*, *>.isNotEmpty(): Boolean = !isEmpty()

context(EE)
public fun <E, EE: Equality<E>> KoneContextualCollection<E, EE>.containsAll(elements: KoneContextualIterableCollection<E, *>): Boolean {
    for (e in elements) if (e !in this) return false
    return true
}

public inline fun <E> KoneContextualRemovableCollection<E, *>.retainAllThat(crossinline predicate: (E) -> Boolean) {
    removeAllThat { !predicate(it) }
}

context(EE)
public fun <E, EE: Equality<E>> KoneContextualRemovableCollection<E, *>.removeAllFrom(elements: KoneContextualCollection<E, EE>) {
    removeAllThat { it in elements }
}

context(EE)
public fun <E, EE: Equality<E>> KoneContextualRemovableCollection<E, *>.retainAllFrom(elements: KoneContextualCollection<E, EE>) {
    retainAllThat { it in elements }
}

context(Equality<E>)
public fun <E> KoneContextualList<E, *>.indexOf(element: E): UInt = indexThat { _, collectionElement -> collectionElement eq element }

context(Equality<E>)
public fun <E> KoneContextualList<E, *>.lastIndexOf(element: E): UInt = lastIndexThat { _, collectionElement -> collectionElement eq element }