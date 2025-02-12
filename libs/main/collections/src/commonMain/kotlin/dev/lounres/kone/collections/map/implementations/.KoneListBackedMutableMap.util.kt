/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedListProducer
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.defaultEquality


public fun <Key, Value> KoneMutableListBackedMap(
    keyEquality: Equality<Key> = defaultEquality(),
): KoneMutableListBackedMap<Key, Value> =
    KoneMutableListBackedMap(
        keyEquality = keyEquality,
        backingList = KoneArrayResizableLinkedNoddedListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedMap(
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, Value> =
    KoneMutableListBackedMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedMap(
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, Value> =
    KoneMutableListBackedMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedMap(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, Value> =
    KoneMutableListBackedMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public fun <Key, Value> KoneMutableListBackedReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
): KoneMutableListBackedReifiedMap<Key, Value> =
    KoneMutableListBackedReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = KoneArrayResizableLinkedNoddedListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, Value> =
    KoneMutableListBackedReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, Value> =
    KoneMutableListBackedReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedReifiedMap(
    initialCapacity: UInt,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, Value> =
    KoneMutableListBackedReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )