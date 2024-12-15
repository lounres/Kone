/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.defaultHashing
import dev.lounres.kone.comparison.defaultReifiedHashing


public fun <Key, KeyContext: Hashing<Key>, Value> KoneHashResizableMap(keyContext: KeyContext): KoneHashResizableMap<Key, KeyContext, Value> =
    KoneHashResizableMap(size = 0u, keyContext = keyContext)

public fun <Key, Value> KoneHashResizableMap(): KoneHashResizableMap<Key, Hashing<Key>, Value> =
    KoneHashResizableMap(size = 0u, keyContext = defaultHashing())

public fun <Key, KeyContext: ReifiedHashing<Key>, Value> KoneHashResizableReifiedMap(keyContext: KeyContext): KoneHashResizableReifiedMap<Key, KeyContext, Value> =
    KoneHashResizableReifiedMap(size = 0u, keyContext = keyContext)

public inline fun <reified Key, Value> KoneHashResizableReifiedMap(): KoneHashResizableReifiedMap<Key, ReifiedHashing<Key>, Value> =
    KoneHashResizableReifiedMap(size = 0u, keyContext = defaultReifiedHashing())