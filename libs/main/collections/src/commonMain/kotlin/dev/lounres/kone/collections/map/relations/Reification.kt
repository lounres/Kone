/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:Suppress("UNCHECKED_CAST")

package dev.lounres.kone.collections.map.relations

import dev.lounres.kone.collections.map.KoneMap
import dev.lounres.kone.collections.map.KoneMapEntry
import dev.lounres.kone.collections.utils.all
import dev.lounres.kone.relations.Reification
import dev.lounres.kone.relations.reificationException
import dev.lounres.kone.option.Maybe
import dev.lounres.kone.option.None
import dev.lounres.kone.option.Some


internal class KoneMapEntryReification<Key, Value>(
    val keyReification: Reification<Key>,
    val valueReification: Reification<Value>,
) : Reification<KoneMapEntry<Key, Value>> {
    override fun contains(element: Any?): Boolean =
        element is KoneMapEntry<*, *> && element.key in keyReification && element.value in valueReification
    override fun reifyMaybe(element: Any?): Maybe<KoneMapEntry<Key, Value>> =
        if (element is KoneMapEntry<*, *> && element.key in keyReification && element.value in valueReification) Some(element as KoneMapEntry<Key, Value>)
        else None
    override fun reifyOrNull(element: Any?): KoneMapEntry<Key, Value>? =
        if (element is KoneMapEntry<*, *> && element.key in keyReification && element.value in valueReification) element as KoneMapEntry<Key, Value>
        else null
    override fun reify(element: Any?): KoneMapEntry<Key, Value> =
        if (element is KoneMapEntry<*, *> && element.key in keyReification && element.value in valueReification) element as KoneMapEntry<Key, Value>
        else reificationException()
}

public fun <Key, Value> koneMapEntryReification(keyContext: Reification<Key>, valueContext: Reification<Value>): Reification<KoneMapEntry<Key, Value>> =
    KoneMapEntryReification(keyContext, valueContext)

internal class KoneMapReification<Key, Value>(
    val keyReification: Reification<Key>,
    val valueReification: Reification<Value>,
) : Reification<KoneMap<out Key, Value>> {
    override fun contains(element: Any?): Boolean =
        element is KoneMap<*, *> && element.nodesView.all { it.key in keyReification && it.value in valueReification }
    override fun reifyMaybe(element: Any?): Maybe<KoneMap<Key, Value>> =
        if (element is KoneMap<*, *> && element.nodesView.all { it.key in keyReification && it.value in valueReification }) Some(element as KoneMap<Key, Value>)
        else None
    override fun reifyOrNull(element: Any?): KoneMap<Key, Value>? =
        if (element is KoneMap<*, *> && element.nodesView.all { it.key in keyReification && it.value in valueReification }) element as KoneMap<Key, Value>
        else null
    override fun reify(element: Any?): KoneMap<Key, Value> =
        if (element is KoneMap<*, *> && element.nodesView.all { it.key in keyReification && it.value in valueReification }) element as KoneMap<Key, Value>
        else reificationException()
}

public fun <Key, Value> koneMapReification(keyReification: Reification<Key>, valueReification: Reification<Value>): Reification<KoneMap<out Key, Value>> =
    KoneMapReification(keyReification, valueReification)