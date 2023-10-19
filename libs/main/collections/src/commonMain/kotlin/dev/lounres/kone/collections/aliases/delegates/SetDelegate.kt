/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.delegates

import dev.lounres.kone.collections.KoneMutableSet
import dev.lounres.kone.collections.KoneSet
import dev.lounres.kone.collections.delegates.KoneMutableSetDelegate
import dev.lounres.kone.collections.delegates.KoneSetAction


public typealias SetAction<E> = KoneSetAction<E>
public typealias MutableSetDelegate<E> = KoneMutableSetDelegate<E>
public inline fun <E> MutableSetDelegate(
    initial: KoneMutableSet<E> = TODO(),
    crossinline before: (state: KoneSet<E>, action: SetAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: KoneSet<E>, action: SetAction<E>) -> Unit = { _, _ -> },
): MutableSetDelegate<E> = KoneMutableSetDelegate(initial, before, after)