/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.implementations

import dev.lounres.kone.collections.implementations.KoneResizableArrayList


public typealias ResizableArrayList<E> = KoneResizableArrayList<E>
public fun <E> ResizableArrayList(): ResizableArrayList<E> = KoneResizableArrayList()
public fun <E> ResizableArrayList(size: UInt, initializer: (index: UInt) -> E): ResizableArrayList<E> = KoneResizableArrayList(size, initializer)