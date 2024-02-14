/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.implementations

import dev.lounres.kone.collections.implementations.KoneGrowableLinkedArrayList


public typealias GrowableLinkedList<E> = KoneGrowableLinkedArrayList<E>
public fun <E> GrowableLinkedList(): GrowableLinkedList<E> = KoneGrowableLinkedArrayList()
public fun <E> GrowableLinkedList(size: UInt, initializer: (index: UInt) -> E): GrowableLinkedList<E> = KoneGrowableLinkedArrayList(size, initializer)