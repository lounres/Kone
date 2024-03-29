/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.implementations

import dev.lounres.kone.collections.utils.koneMapEntryEquality
import dev.lounres.kone.comparison.Equality


public fun <K, V> KoneMutableListBackedMap(keyContext: Equality<K>, valueContext: Equality<V>): KoneMutableListBackedMap<K, V> =
    KoneMutableListBackedMap(
        keyContext = keyContext,
        valueContext = valueContext,
        entryContext = koneMapEntryEquality(keyContext, valueContext),
        backingList = KoneResizableLinkedArrayList(context = koneMapEntryEquality(keyContext, valueContext)),
    )