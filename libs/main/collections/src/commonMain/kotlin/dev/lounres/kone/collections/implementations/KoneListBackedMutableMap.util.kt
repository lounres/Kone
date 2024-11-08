/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.implementations

import dev.lounres.kone.collections.producers.KoneMutableNoddedListProducer
import dev.lounres.kone.comparison.Equality


public fun <Key, KeyContext: Equality<Key>, Value> KoneMutableListBackedMap(
    keyContext: KeyContext,
    backingListProducer: KoneMutableNoddedListProducer = KoneArrayResizableLinkedNoddedListProducer,
): KoneMutableListBackedMap<Key, KeyContext, Value> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        backingList = backingListProducer.produce(),
    )