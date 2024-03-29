/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.utils

import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.context.invoke


internal open class KoneMapEntryEquality<K, V>(val keyContext: Equality<K>, var valueContext: Equality<V>) : Equality<KoneMapEntry<K, V>> {
    override fun KoneMapEntry<K, V>.equalsTo(other: KoneMapEntry<K, V>): Boolean =
        keyContext { this.key eq other.key } && valueContext { this.value eq other.value }
}

public fun <K, V> koneMapEntryEquality(keyContext: Equality<K>, valueContext: Equality<V>): Equality<KoneMapEntry<K, V>> =
    KoneMapEntryEquality(keyContext, valueContext)