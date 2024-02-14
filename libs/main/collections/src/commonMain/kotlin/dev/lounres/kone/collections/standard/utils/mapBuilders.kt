/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.standard.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.standard.KoneMap
import dev.lounres.kone.collections.standard.KoneMutableMap
import dev.lounres.kone.collections.standard.implementations.EmptyKoneMap
import dev.lounres.kone.collections.standard.set
import dev.lounres.kone.collections.standard.wrappers.asKone


public fun <K, V> emptyKoneMap(): KoneMap<K, V> = @Suppress("UNCHECKED_CAST") (EmptyKoneMap as KoneMap<K, V>)

// TODO: Replace with Kone own implementations
public fun <K, V> koneMapOf(): KoneMap<K, V> = emptyKoneMap()
public fun <K, V> koneMapOf(vararg entries: KoneMap.Entry<K, V>): KoneMap<K, V> = mapOf(*entries.map { it.key to it.value }.toTypedArray()).asKone()
public fun <K, V> koneMutableMapOf(): KoneMutableMap<K, V> = mutableMapOf<K, V>().asKone()
public fun <K, V> koneMutableMapOf(vararg entries: KoneMap.Entry<K, V>): KoneMutableMap<K, V> = mutableMapOf(*entries.map { it.key to it.value }.toTypedArray()).asKone()

// TODO: Optimise for collections (introduce capacity calculation and copy-paste the functions for KoneCollection, KoneList, and KoneIterableList)
public inline fun <E, K, V, D: KoneMutableMap<in K, in V>> KoneIterable<E>.associateTo(destination: D, transform: (E) -> KoneMap.Entry<K, V>): D {
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

public inline fun <E, K, V> KoneIterable<E>.associate(transform: (E) -> KoneMap.Entry<K, V>): KoneMap<K, V> =
    associateTo(koneMutableMapOf(), transform)
public inline fun <E, K> KoneIterable<E>.associateBy(keySelector: (E) -> K): KoneMap<K, E> =
    associateByTo(koneMutableMapOf(), keySelector)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keySelector: (E) -> K, valueTransform: (E) -> V): KoneMap<K, V> =
    associateByTo(koneMutableMapOf(), keySelector, valueTransform)
public inline fun <K, V> KoneIterable<K>.associateWith(valueSelector: (K) -> V): KoneMap<K, V> =
    associateWithTo(koneMutableMapOf(), valueSelector)