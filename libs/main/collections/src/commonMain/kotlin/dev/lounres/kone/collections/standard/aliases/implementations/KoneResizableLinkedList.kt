/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.aliases.implementations

import dev.lounres.kone.collections.standard.implementations.KoneResizableLinkedArrayList


public typealias ResizableLinkedList<E> = KoneResizableLinkedArrayList<E>
public fun <E> ResizableLinkedList(): ResizableLinkedList<E> = KoneResizableLinkedArrayList()
public fun <E> ResizableLinkedList(size: UInt, initializer: (index: UInt) -> E): ResizableLinkedList<E> = KoneResizableLinkedArrayList(size, initializer)