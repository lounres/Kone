/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map.implementations

import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedListProducer
import dev.lounres.kone.collections.list.producers.KoneGrowableMutableNoddedListProducer
import dev.lounres.kone.collections.list.producers.KoneResizableMutableNoddedListProducer
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultReifiedEquality


public fun <Key, Value> KoneMutableListBackedMap(): KoneMutableListBackedMap<Key, Equality<Key>, Value> =
    KoneMutableListBackedMap(
        keyContext = defaultEquality(),
        backingList = KoneArrayResizableLinkedNoddedListProducer.produce(),
    )

public fun <Key, KeyContext: Equality<Key>, Value> KoneMutableListBackedMap(
    keyContext: KeyContext,
): KoneMutableListBackedMap<Key, KeyContext, Value> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        backingList = KoneArrayResizableLinkedNoddedListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedMap(
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, Equality<Key>, Value> =
    KoneMutableListBackedMap(
        keyContext = defaultEquality(),
        backingList = backingListProducer.produce(),
    )

public fun <Key, KeyContext: Equality<Key>, Value> KoneMutableListBackedMap(
    keyContext: KeyContext,
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, KeyContext, Value> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedMap(
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, Equality<Key>, Value> =
    KoneMutableListBackedMap(
        keyContext = defaultEquality(),
        backingList = backingListProducer.produce(),
    )

public fun <Key, KeyContext: Equality<Key>, Value> KoneMutableListBackedMap(
    keyContext: KeyContext,
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, KeyContext, Value> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(),
    )

public fun <Key, Value> KoneMutableListBackedMap(
    initialCapacity: UInt,
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, Equality<Key>, Value> =
    KoneMutableListBackedMap(
        keyContext = defaultEquality(),
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public fun <Key, KeyContext: Equality<Key>, Value> KoneMutableListBackedMap(
    initialCapacity: UInt,
    keyContext: KeyContext,
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedMap<Key, KeyContext, Value> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public inline fun <reified Key, Value> KoneMutableListBackedReifiedMap(): KoneMutableListBackedReifiedMap<Key, ReifiedEquality<Key>, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = defaultReifiedEquality(),
        backingList = KoneArrayResizableLinkedNoddedListProducer.produce(),
    )

public fun <Key, KeyContext: ReifiedEquality<Key>, Value> KoneMutableListBackedReifiedMap(
    keyContext: KeyContext,
): KoneMutableListBackedReifiedMap<Key, KeyContext, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = keyContext,
        backingList = KoneArrayResizableLinkedNoddedListProducer.produce(),
    )

public inline fun <reified Key, Value> KoneMutableListBackedReifiedMap(
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, ReifiedEquality<Key>, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = defaultReifiedEquality(),
        backingList = backingListProducer.produce(),
    )

public fun <Key, KeyContext: ReifiedEquality<Key>, Value> KoneMutableListBackedReifiedMap(
    keyContext: KeyContext,
    backingListProducer: KoneResizableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, KeyContext, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(),
    )

public inline fun <reified Key, Value> KoneMutableListBackedReifiedMap(
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, ReifiedEquality<Key>, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = defaultReifiedEquality(),
        backingList = backingListProducer.produce(),
    )

public fun <Key, KeyContext: ReifiedEquality<Key>, Value> KoneMutableListBackedReifiedMap(
    keyContext: KeyContext,
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, KeyContext, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(),
    )

public inline fun <reified Key, Value> KoneMutableListBackedReifiedMap(
    initialCapacity: UInt,
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, ReifiedEquality<Key>, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = defaultReifiedEquality(),
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )

public fun <Key, KeyContext: ReifiedEquality<Key>, Value> KoneMutableListBackedReifiedMap(
    initialCapacity: UInt,
    keyContext: KeyContext,
    backingListProducer: KoneGrowableMutableNoddedListProducer,
): KoneMutableListBackedReifiedMap<Key, KeyContext, Value> =
    KoneMutableListBackedReifiedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(initialCapacity = initialCapacity),
    )