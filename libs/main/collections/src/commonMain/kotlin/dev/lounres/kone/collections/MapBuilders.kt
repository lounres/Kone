/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.EmptyKoneMap
import dev.lounres.kone.collections.implementations.KoneMutableListBackedMap
import dev.lounres.kone.collections.implementations.KoneResizableHashMap
import dev.lounres.kone.collections.utils.indices
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality


@Suppress("UNCHECKED_CAST")
public fun <K, V> emptyKoneMap(): KoneMap<K, V> = EmptyKoneMap as KoneMap<K, V>

@Suppress("unused")
public fun <K, V> koneMapOf(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMap<K, V> =
    emptyKoneMap()

public fun <K, V> koneMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMap<K, V> =
    when {
        entries.isEmpty() -> emptyKoneMap()
        keyContext is Hashing -> KoneResizableHashMap(keyContext = keyContext, valueContext = valueContext).apply {
            setAllFrom(KoneArray(entries))
        }
        else -> KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext).apply {
            setAllFrom(KoneArray(entries))
        }
    }


public fun <K, V> koneMutableMapOf(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMutableMap<K, V> =
    if (keyContext is Hashing<K>) KoneResizableHashMap(keyContext = keyContext, valueContext = valueContext)
    else KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext)

public fun <K, V> koneMutableMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMutableMap<K, V> =
    if (keyContext is Hashing<K>) KoneResizableHashMap(keyContext = keyContext, valueContext = valueContext).apply { setAllFrom(KoneArray(entries)) }
    else KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext).apply { setAllFrom(KoneArray(entries)) }

public inline fun <E, K, V, D: KoneMutableMap<in K, in V>> KoneIterable<E>.associateTo(destination: D, transform: (E) -> KoneMapEntry<K, V>): D {
    for (element in this) destination.set(transform(element))
    return destination
}

public inline fun <E, K, V, D: KoneMutableMap<in K, in V>> KoneList<E>.associateTo(destination: D, transform: (E) -> KoneMapEntry<K, V>): D {
    for (index in indices) destination.set(transform(this[index]))
    return destination
}

public inline fun <E, K, V, D: KoneMutableMap<in K, in V>> KoneIterableList<E>.associateTo(destination: D, transform: (E) -> KoneMapEntry<K, V>): D {
    for (element in this) destination.set(transform(element))
    return destination
}

public inline fun <E, K, D : KoneMutableMap<in K, in E>> KoneIterable<E>.associateByTo(destination: D, keySelector: (E) -> K): D {
    for (element in this) destination[keySelector(element)] = element
    return destination
}

public inline fun <E, K, D : KoneMutableMap<in K, in E>> KoneList<E>.associateByTo(destination: D, keySelector: (E) -> K): D {
    for (index in indices) {
        val element = this[index]
        destination[keySelector(element)] = element
    }
    return destination
}

public inline fun <E, K, D : KoneMutableMap<in K, in E>> KoneIterableList<E>.associateByTo(destination: D, keySelector: (E) -> K): D {
    for (element in this) destination[keySelector(element)] = element
    return destination
}

public inline fun <E, K, V, D : KoneMutableMap<in K, in V>> KoneIterable<E>.associateByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) destination[keySelector(element)] = valueTransform(element)
    return destination
}

public inline fun <E, K, V, D : KoneMutableMap<in K, in V>> KoneList<E>.associateByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (index in indices) {
        val element = this[index]
        destination[keySelector(element)] = valueTransform(element)
    }
    return destination
}

public inline fun <E, K, V, D : KoneMutableMap<in K, in V>> KoneIterableList<E>.associateByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) destination[keySelector(element)] = valueTransform(element)
    return destination
}

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneIterable<K>.associateWithTo(destination: D, valueSelector: (K) -> V): D {
    for (element in this) destination[element] = valueSelector(element)
    return destination
}

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneList<K>.associateWithTo(destination: D, valueSelector: (K) -> V): D {
    for (index in indices) {
        val element = this[index]
        destination[element] = valueSelector(element)
    }
    return destination
}

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneIterableList<K>.associateWithTo(destination: D, valueSelector: (K) -> V): D {
    for (element in this) destination[element] = valueSelector(element)
    return destination
}

public inline fun <E, K, V> KoneIterable<E>.associate(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), transform: (E) -> KoneMapEntry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), transform)

public inline fun <E, K> KoneIterable<E>.associateBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector)

public inline fun <E, K, V> KoneIterable<E>.associateBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector, valueTransform)

public inline fun <K, V> KoneIterable<K>.associateWith(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), valueSelector)