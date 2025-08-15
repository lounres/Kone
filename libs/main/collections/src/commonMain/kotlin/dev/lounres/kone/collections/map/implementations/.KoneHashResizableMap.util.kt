/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultFor


public fun <Key, Value> KoneHashResizableMap(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key> = Hashing.defaultFor(),
): KoneHashResizableMap<Key, Value> =
    KoneHashResizableMap(size = 0u, keyEquality = keyEquality, keyHashing = keyHashing)

public fun <Key, Value> KoneHashResizableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key> = Hashing.defaultFor(),
): KoneHashResizableReifiedMap<Key, Value> =
    KoneHashResizableReifiedMap(size = 0u, keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)

public inline fun <reified Key, Value> KoneHashResizableReifiedMap(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key> = Hashing.defaultFor(),
): KoneHashResizableReifiedMap<Key, Value> =
    KoneHashResizableReifiedMap(size = 0u, keyReification = Reification.defaultFor(), keyEquality = keyEquality, keyHashing = keyHashing)