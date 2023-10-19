/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.implementations

import dev.lounres.kone.collections.implementations.KoneGrowableLinkedList


public typealias GrowableLinkedList<E> = KoneGrowableLinkedList<E>
public fun <E> GrowableLinkedList(): GrowableLinkedList<E> = KoneGrowableLinkedList()
public fun <E> GrowableLinkedList(size: UInt, initializer: (index: UInt) -> E): GrowableLinkedList<E> = KoneGrowableLinkedList(size, initializer)