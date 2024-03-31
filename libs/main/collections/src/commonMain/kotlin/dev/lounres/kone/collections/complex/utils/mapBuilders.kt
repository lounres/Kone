/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.complex.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.complex.KoneMap
import dev.lounres.kone.collections.complex.KoneMutableMap
import dev.lounres.kone.collections.complex.implementations.EmptyKoneMap
import dev.lounres.kone.collections.complex.implementations.KoneMutableListBackedMap
import dev.lounres.kone.collections.complex.set
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.wrappers.asKone
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultHashing


@Suppress("UNCHECKED_CAST")
public fun <K, V> emptyKoneMap(): KoneMap<K, V> = EmptyKoneMap as KoneMap<K, V>

// TODO: Replace with Kone own implementations
public fun <K, V> koneMapOf(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMap<K, V> =
    emptyKoneMap()
public fun <K, V> koneMapOf(keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V> = defaultEquality()): KoneMap<K, V> =
    emptyKoneMap()
public fun <K, V> koneMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext).apply {
        setAll(entries.asIterable().asKone()) // TODO: Optimize
    }
public fun <K, V> koneMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V> = defaultEquality()): KoneMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext).apply { // TODO: Replace with hashing map
        setAll(entries.asIterable().asKone()) // TODO: Optimize
    }
public fun <K, V> koneMutableMapOf(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> koneMutableMapOf(keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V> = defaultEquality()): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext) // TODO: Replace with hashing map
public fun <K, V> koneMutableMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality()): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> koneMutableMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V> = defaultEquality()): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext) // TODO: Replace with hashing map

// TODO: Optimise for collections (introduce capacity calculation and copy-paste the functions for KoneCollection, KoneList, and KoneIterableList)
public inline fun <E, K, V, D: KoneMutableMap<in K, in V>> KoneIterable<E>.associateTo(destination: D, transform: (E) -> KoneMapEntry<K, V>): D {
    for (element in this) {
        destination.set(transform(element))
    }
    return destination
}
public inline fun <E, K, D : KoneMutableMap<in K, in E>> KoneIterable<E>.associateByTo(destination: D, keySelector: (E) -> K): D {
    for (element in this) {
        destination[keySelector(element)] = element
    }
    return destination
}
public inline fun <E, K, V, D : KoneMutableMap<in K, in V>> KoneIterable<E>.associateByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) {
        destination[keySelector(element)] = valueTransform(element)
    }
    return destination
}
public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneIterable<K>.associateWithTo(destination: D, valueSelector: (K) -> V): D {
    for (element in this) {
        destination[element] = valueSelector(element)
    }
    return destination
}

public inline fun <E, K, V> KoneIterable<E>.associate(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), transform: (E) -> KoneMapEntry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), transform)
public inline fun <E, K, V> KoneIterable<E>.associate(keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V> = defaultEquality(), transform: (E) -> KoneMapEntry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), transform)
public inline fun <E, K> KoneIterable<E>.associateBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector)
public inline fun <E, K> KoneIterable<E>.associateBy(keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<E> = defaultEquality(), keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector, valueTransform)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V> = defaultEquality(), keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector, valueTransform)
public inline fun <K, V> KoneIterable<K>.associateWith(keyContext: Equality<K> = defaultEquality(), valueContext: Equality<V> = defaultEquality(), valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), valueSelector)
public inline fun <K, V> KoneIterable<K>.associateWith(keyContext: Hashing<K> = defaultHashing(), valueContext: Equality<V>, valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), valueSelector)