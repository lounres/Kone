/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.implementations

import dev.lounres.kone.collections.implementations.KoneSettableArrayList


public typealias SettableArrayList<E> = KoneSettableArrayList<E>
public fun <E> SettableArrayList(size: UInt, initializer: (index: UInt) -> E): SettableArrayList<E> = KoneSettableArrayList(size, initializer)