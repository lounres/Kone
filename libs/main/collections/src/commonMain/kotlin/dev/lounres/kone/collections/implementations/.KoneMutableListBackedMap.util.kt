/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMutableList
import dev.lounres.kone.comparison.Equality


public inline fun <K, KC: Equality<K>, V> KoneMutableListBackedMap(
    keyContext: KC,
    backingListFabric: () -> KoneMutableList<KoneMapEntry<K, V>>,
): KoneMutableListBackedMap<K, KC, V> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        backingList = backingListFabric(),
    )

public fun <K, KC: Equality<K>, V> KoneMutableListBackedMap(keyContext: KC): KoneMutableListBackedMap<K, KC, V> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
    ) { KoneResizableLinkedArrayList() }