/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.KoneMutableIterableList
import dev.lounres.kone.collections.koneMapEntryEquality
import dev.lounres.kone.comparison.Equality


public inline fun <K, KC: Equality<K>, V, VC: Equality<V>> KoneMutableListBackedMap(
    keyContext: KC,
    valueContext: VC,
    backingListFabric: (Equality<KoneMapEntry<K, V>>) -> KoneMutableIterableList<KoneMapEntry<K, V>>,
): KoneMutableListBackedMap<K, KC, V, VC> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        valueContext = valueContext,
        backingList = backingListFabric(koneMapEntryEquality(keyContext, valueContext)),
    )

public fun <K, KC: Equality<K>, V, VC: Equality<V>> KoneMutableListBackedMap(keyContext: KC, valueContext: VC): KoneMutableListBackedMap<K, KC, V, VC> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        valueContext = valueContext,
    ) { KoneResizableLinkedArrayList(elementContext = it) }