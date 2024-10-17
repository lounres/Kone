/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneIterableList
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.koneMapEntryEquality
import dev.lounres.kone.comparison.Equality


public inline fun <K, KC: Equality<K>, V, VC: Equality<V>> KoneListBackedMap(
    keyContext: KC,
    valueContext: VC,
    backingListFabric: (Equality<KoneMapEntry<K, V>>) -> KoneIterableList<KoneMapEntry<K, V>>
): KoneListBackedMap<K, KC, V, VC> =
    KoneListBackedMap(keyContext = keyContext, valueContext = valueContext, backingListFabric(koneMapEntryEquality(keyContext, valueContext)))