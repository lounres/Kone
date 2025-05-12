/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultEquality
import dev.lounres.kone.relations.defaultHashing


public fun <Key, Value> KoneHashResizableMap(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key> = defaultHashing(),
): KoneHashResizableMap<Key, Value> =
    KoneHashResizableMap(size = 0u, keyEquality = keyEquality, keyHashing = keyHashing)

public fun <Key, Value> KoneHashResizableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key> = defaultHashing(),
): KoneHashResizableReifiedMap<Key, Value> =
    KoneHashResizableReifiedMap(size = 0u, keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)

public inline fun <reified Key, Value> KoneHashResizableReifiedMap(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key> = defaultHashing(),
): KoneHashResizableReifiedMap<Key, Value> =
    KoneHashResizableReifiedMap(size = 0u, keyReification = Reification(), keyEquality = keyEquality, keyHashing = keyHashing)