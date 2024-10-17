/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing


public fun <K, KC: Hashing<K>, V, VC: Equality<V>> KoneResizableHashMap(keyContext: KC, valueContext: VC): KoneResizableHashMap<K, KC, V, VC> =
    KoneResizableHashMap(size = 0u, keyContext = keyContext, valueContext = valueContext)