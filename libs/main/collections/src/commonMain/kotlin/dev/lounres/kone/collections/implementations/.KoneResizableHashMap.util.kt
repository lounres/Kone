/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultHashing


public fun <K, KC: Hashing<K>, V> KoneResizableHashMap(keyContext: KC): KoneResizableHashMap<K, KC, V> =
    KoneResizableHashMap(size = 0u, keyContext = keyContext)

public fun <K, V> KoneResizableHashMap(): KoneResizableHashMap<K, Hashing<K>, V> =
    KoneResizableHashMap(size = 0u, keyContext = defaultHashing())