/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.common.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.common.KoneMap
import dev.lounres.kone.collections.common.KoneMutableMap
import dev.lounres.kone.collections.common.implementations.EmptyKoneMap
import dev.lounres.kone.collections.common.implementations.KoneMutableListBackedMap
import dev.lounres.kone.collections.common.set
import dev.lounres.kone.collections.wrappers.asKone


public fun <K, V> emptyKoneMap(): KoneMap<K, V> = @Suppress("UNCHECKED_CAST") (EmptyKoneMap as KoneMap<K, V>)

// TODO: Replace with Kone own implementations
public fun <K, V> koneMapOf(): KoneMap<K, V> = emptyKoneMap()
public fun <K, V> koneMapOf(vararg entries: KoneMapEntry<K, V>): KoneMap<K, V> =
    KoneMutableListBackedMap<K, V>().apply { setAll(entries.asIterable().asKone()) } // TODO: Optimize
public fun <K, V> koneMutableMapOf(): KoneMutableMap<K, V> = KoneMutableListBackedMap<K, V>()
public fun <K, V> koneMutableMapOf(vararg entries: KoneMapEntry<K, V>): KoneMutableMap<K, V> = TODO()

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

public inline fun <E, K, V> KoneIterable<E>.associate(transform: (E) -> KoneMapEntry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(), transform)
public inline fun <E, K> KoneIterable<E>.associateBy(keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(), keySelector)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(), keySelector, valueTransform)
public inline fun <K, V> KoneIterable<K>.associateWith(valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(), valueSelector)