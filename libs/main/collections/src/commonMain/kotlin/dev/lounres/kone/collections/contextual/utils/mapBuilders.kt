/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.contextual.utils

import dev.lounres.kone.collections.KoneIterable
import dev.lounres.kone.collections.KoneMapEntry
import dev.lounres.kone.collections.common.implementations.EmptyKoneMap
import dev.lounres.kone.collections.next
import dev.lounres.kone.collections.contextual.KoneContextualMap
import dev.lounres.kone.collections.contextual.KoneContextualMutableMap
import dev.lounres.kone.collections.contextual.implementations.KoneContextualMutableListBackedMap
import dev.lounres.kone.collections.contextual.set
import dev.lounres.kone.collections.wrappers.asKone
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing


public fun <K, KE: Equality<K>, V> emptyKoneContextualMap(): KoneContextualMap<K, KE, V> = @Suppress("UNCHECKED_CAST") (EmptyKoneMap as KoneContextualMap<K, KE, V>)

// TODO: Replace with Kone own implementations
public fun <K, V> koneContextualMapOf(): KoneContextualMap<K, Equality<K>, V> = emptyKoneContextualMap()
context(Equality<K>)
public fun <K, V> koneContextualMapOf(vararg entries: KoneMapEntry<K, V>): KoneContextualMap<K, Equality<K>, V> =
    KoneContextualMutableListBackedMap<K, V>().apply { setAll(entries.asIterable().asKone()) } // TODO: Optimize
public fun <K, V> koneContextualMutableMapOf(): KoneContextualMutableMap<K, Equality<K>, V> = KoneContextualMutableListBackedMap()
context(Equality<K>)
public fun <K, V> koneContextualMutableMapOf(vararg entries: KoneMapEntry<K, V>): KoneContextualMutableMap<K, Equality<K>, V> = KoneContextualMutableListBackedMap<K, V>().apply { setAll(entries.asIterable().asKone()) }

// TODO: Optimise for collections (introduce capacity calculation and copy-paste the functions for KoneCollection, KoneList, and KoneIterableList)
context(KE)
public inline fun <E, K, KE: Equality<K>, V, D: KoneContextualMutableMap<in K, KE, in V>> KoneIterable<E>.associateTo(destination: D, transform: (E) -> KoneMapEntry<K, V>): D {
    for (element in this) {
        destination.set(transform(element))
    }
    return destination
}
context(KE)
public inline fun <E, K, KE: Equality<K>, D : KoneContextualMutableMap<in K, KE, in E>> KoneIterable<E>.associateByTo(destination: D, keySelector: (E) -> K): D {
    for (element in this) {
        destination[keySelector(element)] = element
    }
    return destination
}
context(KE)
public inline fun <E, K, KE: Equality<K>, V, D : KoneContextualMutableMap<in K, KE, in V>> KoneIterable<E>.associateByTo(destination: D, keySelector: (E) -> K, valueTransform: (E) -> V): D {
    for (element in this) {
        destination[keySelector(element)] = valueTransform(element)
    }
    return destination
}
context(KE)
public inline fun <K, KE: Equality<K>, V, D : KoneContextualMutableMap<in K, KE, in V>> KoneIterable<K>.associateWithTo(destination: D, valueSelector: (K) -> V): D {
    for (element in this) {
        destination[element] = valueSelector(element)
    }
    return destination
}

context(Equality<K>)
public inline fun <E, K, V> KoneIterable<E>.associate(transform: (E) -> KoneMapEntry<K, V>): KoneContextualMap<K, Equality<K>, V> =
    associateTo(koneContextualMutableMapOf(), transform)
context(Equality<K>)
public inline fun <E, K> KoneIterable<E>.associateBy(keySelector: (E) -> K): KoneContextualMap<K, Equality<K>, E> =
    associateByTo(koneContextualMutableMapOf(), keySelector)
context(Equality<K>)
public inline fun <E, K, V> KoneIterable<E>.associateBy(keySelector: (E) -> K, valueTransform: (E) -> V): KoneContextualMap<K, Equality<K>, V> =
    associateByTo(koneContextualMutableMapOf(), keySelector, valueTransform)
context(Equality<K>)
public inline fun <K, V> KoneIterable<K>.associateWith(valueSelector: (K) -> V): KoneContextualMap<K, Equality<K>, V> =
    associateWithTo(koneContextualMutableMapOf(), valueSelector)