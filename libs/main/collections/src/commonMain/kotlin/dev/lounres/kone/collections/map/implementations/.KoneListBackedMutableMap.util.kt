/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedList
import dev.lounres.kone.collections.list.contexts.KoneFixedCapacityMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.contexts.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.defaultFor


public fun <Key, Value> KoneListBackedMutableMap(
    keyEquality: Equality<Key> = Equality.defaultFor(),
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = KoneArrayResizableLinkedNoddedList(),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public fun <Key, Value> KoneListBackedMutableMap(
    capacity: UInt,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    backingListProducer: KoneFixedCapacityMutableNoddedListProducer,
): KoneListBackedMutableMap<Key, Value> =
    KoneListBackedMutableMap(
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(capacity = capacity),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = KoneArrayResizableLinkedNoddedList(),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneListBackedMutableReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
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
    keyEquality: Equality<Key> = Equality.defaultFor(),
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
    keyEquality: Equality<Key> = Equality.defaultFor(),
    backingListProducer: KoneFixedCapacityMutableNoddedListProducer,
): KoneListBackedMutableReifiedMap<Key, Value> =
    KoneListBackedMutableReifiedMap(
        keyReification = keyReification,
        keyEquality = keyEquality,
        backingList = backingListProducer.produce(capacity = capacity),
    )