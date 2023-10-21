/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.delegates

import dev.lounres.kone.collections.KoneList
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.collections.delegates.KoneMutableListDelegate
import dev.lounres.kone.collections.delegates.KoneListAction
import dev.lounres.kone.collections.implementations.KoneGrowableArrayList


public typealias ListAction<E> = KoneListAction<E>
public typealias MutableListDelegate<E> = KoneMutableListDelegate<E>
public inline fun <E> MutableListDelegate(
    initial: KoneMutableList<E> = KoneGrowableArrayList(),
    crossinline before: (state: KoneList<E>, action: ListAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneList<E>, action: ListAction<E>) -> Unit = { _, _ -> },
): MutableListDelegate<E> = KoneMutableListDelegate(initial, before, after)