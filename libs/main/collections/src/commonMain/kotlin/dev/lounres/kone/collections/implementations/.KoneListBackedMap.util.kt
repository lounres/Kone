/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.producers.KoneListProducer
import dev.lounres.kone.comparison.Equality


public fun <Key, KeyContext: Equality<Key>, Value> KoneListBackedMap(
    keyContext: KeyContext,
    listProducer: KoneListProducer = KoneArraySettableListProducer
): KoneListBackedMap<Key, KeyContext, Value> =
    KoneListBackedMap(keyContext = keyContext, listProducer.produce())

public inline fun <Key, KeyContext: Equality<Key>, Value> KoneListBackedMap(
    keyContext: KeyContext,
    size: UInt,
    listProducer: KoneListProducer = KoneArraySettableListProducer,
    crossinline elementBuilder: (UInt) -> KoneMapEntry<Key, Value>
): KoneListBackedMap<Key, KeyContext, Value> =
    KoneListBackedMap(
        keyContext = keyContext,
        backingList = listProducer.produceBy(size) { elementBuilder(it).let { KoneListBackedMap.Node(key = it.key, value = it.value) } }
    )