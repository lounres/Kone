/*
 * Copyright © 2023 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.aliases.delegates

import dev.lounres.kone.collections.aliases.IterableSet
import dev.lounres.kone.collections.aliases.MutableIterableSet
import dev.lounres.kone.collections.delegates.KoneExtendableIterableSetDelegate
import dev.lounres.kone.collections.delegates.KoneMutableIterableSetDelegate
import dev.lounres.kone.collections.delegates.KoneRemovableIterableSetDelegate
import dev.lounres.kone.collections.delegates.KoneSetAction


public typealias SetAction<E> = KoneSetAction<E>

public typealias ExtendableIterableSetDelegate<E> = KoneExtendableIterableSetDelegate<E>
public inline fun <E> ExtendableIterableSetDelegate(
    initial: MutableIterableSet<E> = TODO(),
    crossinline before: (state: IterableSet<E>, action: KoneSetAction.Extending<E>) -> Unit = { _, _ -> },
    crossinline after: (state: IterableSet<E>, action: KoneSetAction.Extending<E>) -> Unit = { _, _ -> },
): ExtendableIterableSetDelegate<E> = KoneExtendableIterableSetDelegate(initial, before, after)

public typealias RemovableIterableSetDelegate<E> = KoneRemovableIterableSetDelegate<E>
public inline fun <E> RemovableIterableSetDelegate(
    initial: MutableIterableSet<E> = TODO(),
    crossinline before: (state: IterableSet<E>, action: KoneSetAction.Removing<E>) -> Unit = { _, _ -> },
    crossinline after: (state: IterableSet<E>, action: KoneSetAction.Removing<E>) -> Unit = { _, _ -> },
): RemovableIterableSetDelegate<E> = KoneRemovableIterableSetDelegate(initial, before, after)

public typealias MutableIterableSetDelegate<E> = KoneMutableIterableSetDelegate<E>
public inline fun <E> MutableIterableSetDelegate(
    initial: MutableIterableSet<E> = TODO(),
    crossinline before: (state: IterableSet<E>, action: SetAction<E>) -> Unit = { _, _ -> },
    crossinline after: (state: IterableSet<E>, action: SetAction<E>) -> Unit = { _, _ -> },
): MutableIterableSetDelegate<E> = KoneMutableIterableSetDelegate(initial, before, after)