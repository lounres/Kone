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


public fun <K, V> emptyKoneMap(keyContext: Equality<K>, valueContext: Equality<V>, ): KoneMap<K, V> =
    EmptyKoneMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> emptyKoneMap(keyContext: Hashing<K>, valueContext: Equality<V>, ): KoneMap<K, V> =
    EmptyKoneMap(keyContext = keyContext, valueContext = valueContext) // TODO: Replace with hashing map

// TODO: Replace with Kone own implementations
public fun <K, V> koneMapOf(keyContext: Equality<K>, valueContext: Equality<V>): KoneMap<K, V> =
    emptyKoneMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> koneMapOf(keyContext: Hashing<K>, valueContext: Equality<V>): KoneMap<K, V> =
    emptyKoneMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> koneMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Equality<K>, valueContext: Equality<V>): KoneMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext).apply {
        setAll(entries.asIterable().asKone()) // TODO: Optimize
    }
public fun <K, V> koneMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Hashing<K>, valueContext: Equality<V>): KoneMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext).apply { // TODO: Replace with hashing map
        setAll(entries.asIterable().asKone()) // TODO: Optimize
    }
public fun <K, V> koneMutableMapOf(keyContext: Equality<K>, valueContext: Equality<V>): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> koneMutableMapOf(keyContext: Hashing<K>, valueContext: Equality<V>): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext) // TODO: Replace with hashing map
public fun <K, V> koneMutableMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Equality<K>, valueContext: Equality<V>): KoneMutableMap<K, V> =
    KoneMutableListBackedMap(keyContext = keyContext, valueContext = valueContext)
public fun <K, V> koneMutableMapOf(vararg entries: KoneMapEntry<K, V>, keyContext: Hashing<K>, valueContext: Equality<V>): KoneMutableMap<K, V> =
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

public inline fun <E, K, V> KoneIterable<E>.associate(keyContext: Equality<K>, valueContext: Equality<V>, transform: (E) -> KoneMapEntry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), transform)
public inline fun <E, K, V> KoneIterable<E>.associate(keyContext: Hashing<K>, valueContext: Equality<V>, transform: (E) -> KoneMapEntry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), transform)
public inline fun <E, K> KoneIterable<E>.associateBy(keyContext: Equality<K>, valueContext: Equality<E>, keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector)
public inline fun <E, K> KoneIterable<E>.associateBy(keyContext: Hashing<K>, valueContext: Equality<E>, keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keyContext: Equality<K>, valueContext: Equality<V>, keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector, valueTransform)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keyContext: Hashing<K>, valueContext: Equality<V>, keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), keySelector, valueTransform)
public inline fun <K, V> KoneIterable<K>.associateWith(keyContext: Equality<K>, valueContext: Equality<V>, valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), valueSelector)
public inline fun <K, V> KoneIterable<K>.associateWith(keyContext: Hashing<K>, valueContext: Equality<V>, valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext, valueContext = valueContext), valueSelector)