/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing


public fun <Key, KeyContext: Hashing<Key>, Value> KoneResizableHashMap(keyContext: KeyContext): KoneResizableHashMap<Key, KeyContext, Value> =
    KoneResizableHashMap(size = 0u, keyContext = keyContext)

public fun <Key, Value> KoneResizableHashMap(): KoneResizableHashMap<Key, Hashing<Key>, Value> =
    KoneResizableHashMap(size = 0u, keyContext = defaultHashing())