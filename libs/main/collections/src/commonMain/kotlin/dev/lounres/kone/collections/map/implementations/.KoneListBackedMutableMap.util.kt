/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedList
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultEquality


public fun <Key, Value> KoneListBackedMutableMap(
    keyEquality: Equality<Key> = defaultEquality(),
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = KoneArrayResizableLinkedNoddedList(),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    capacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneFixedCapacityMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(capacity = capacity),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = KoneArrayResizableLinkedNoddedList(),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    initialCapacity: UInt,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    capacity: UInt,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    backingListProducer: KoneFixedCapacityMutableNoddedListProducer,
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(capacity = capacity),
    )