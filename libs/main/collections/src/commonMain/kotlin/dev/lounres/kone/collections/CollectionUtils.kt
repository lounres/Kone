/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public inline fun <E> KoneRemovableCollection<E>.retainAllThat(crossinline predicate: (E) -> Boolean) {
    removeAllThat { !predicate(it) }
}

public fun <E> KoneRemovableCollection<E>.removeAllFrom(elements: KoneCollection<E>) {
    removeAllThat { it in elements }
}

public fun <E> KoneRemovableCollection<E>.retainAllFrom(elements: KoneCollection<E>) {
    retainAllThat { it in elements }
}