/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.implementations

import dev.lounres.kone.collections.implementations.KoneResizableLinkedList


public typealias ResizableLinkedList<E> = KoneResizableLinkedList<E>
public fun <E> ResizableLinkedList(): ResizableLinkedList<E> = KoneResizableLinkedList()
public fun <E> ResizableLinkedList(size: UInt, initializer: (index: UInt) -> E): ResizableLinkedList<E> = KoneResizableLinkedList(size, initializer)