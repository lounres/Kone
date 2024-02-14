/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.aliases.implementations

import dev.lounres.kone.collections.standard.implementations.KoneGrowableArrayList


public typealias GrowableArrayList<E> = KoneGrowableArrayList<E>
public fun <E> GrowableArrayList(): GrowableArrayList<E> = KoneGrowableArrayList()
public fun <E> GrowableArrayList(size: UInt, initializer: (index: UInt) -> E): GrowableArrayList<E> = KoneGrowableArrayList(size, initializer)