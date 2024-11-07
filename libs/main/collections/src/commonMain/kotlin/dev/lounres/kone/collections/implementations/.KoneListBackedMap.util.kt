/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.producers.KoneListProducer
import dev.lounres.kone.comparison.Equality


public fun <K, KC: Equality<K>, V> KoneListBackedMap(
    keyContext: KC,
    listProducer: KoneListProducer = KoneSettableArrayListProducer
): KoneListBackedMap<K, KC, V> =
    KoneListBackedMap(keyContext = keyContext, listProducer.produce())

public fun <K, KC: Equality<K>, V> KoneListBackedMap(
    keyContext: KC,
    size: UInt,
    listProducer: KoneListProducer = KoneSettableArrayListProducer,
    elementBuilder: (UInt) -> KoneMapEntry<K, V>
): KoneListBackedMap<K, KC, V> =
    KoneListBackedMap(
        keyContext = keyContext,
        backingList = listProducer.produceBy(size) { elementBuilder(it).let { KoneListBackedMap.Node(key = it.key, value = it.value) } }
    )