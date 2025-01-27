/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalTypeInference::class)

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableLinkedNoddedListProducer
import dev.lounres.kone.collections.map.empty.KoneEmptyReifiedMap
import dev.lounres.kone.collections.map.implementations.KoneMutableListBackedMap
import dev.lounres.kone.collections.map.implementations.KoneHashResizableMap
import dev.lounres.kone.collections.map.implementations.KoneHashResizableReifiedMap
import dev.lounres.kone.collections.map.implementations.KoneMutableListBackedReifiedMap
import dev.lounres.kone.collections.map.singleton.KoneSingletonMap
import dev.lounres.kone.collections.map.singleton.KoneSingletonReifiedMap
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedListProducer
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.ReifiedEquality
import dev.lounres.kone.comparison.ReifiedHashing
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.defaultReifiedEquality
import kotlin.contracts.InvocationKind
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders with vararg map nodes

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> emptyKoneMap(): KoneMap<Key, Value> = KoneEmptyReifiedMap as KoneMap<Key, Value>

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> emptyKoneReifiedMap(): KoneReifiedMap<Key, Value> = KoneEmptyReifiedMap as KoneReifiedMap<Key, Value>

@Suppress("unused")
public fun <Key, Value> koneMapOf(keyContext: Equality<Key> = defaultEquality()): KoneMap<Key, Value> =
    emptyKoneMap()

@Suppress("unused")
public inline fun <reified Key, Value> koneReifiedMapOf(): KoneReifiedMap<Key, Value> = emptyKoneReifiedMap()

@Suppress("unused")
public fun <Key, Value> koneReifiedMapOf(keyContext: ReifiedEquality<Key>): KoneReifiedMap<Key, Value> =
    emptyKoneReifiedMap()

public fun <Key, Value> koneMapOf(entry: KoneMapEntry<Key, Value>, keyContext: Equality<Key> = defaultEquality()): KoneMap<Key, Value> =
    KoneSingletonMap(
        singleKey = entry.key,
        singleValue = entry.value,
        keyContext = keyContext,
    )

public inline fun <reified Key, Value> koneReifiedMapOf(entry: KoneMapEntry<Key, Value>): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(entry = entry, keyContext = defaultReifiedEquality())

public fun <Key, Value> koneReifiedMapOf(entry: KoneMapEntry<Key, Value>, keyContext: ReifiedEquality<Key>): KoneReifiedMap<Key, Value> =
    KoneSingletonReifiedMap(
        singleKey = entry.key,
        singleValue = entry.value,
        keyContext = keyContext,
    )

public fun <Key, Value> koneMapOf(vararg entries: KoneMapEntry<Key, Value>, keyContext: Equality<Key> = defaultEquality()): KoneMap<Key, Value> =
    when {
        entries.isEmpty() -> emptyKoneMap()
        keyContext is Hashing -> KoneHashResizableMap<Key, _, Value>(keyContext = keyContext).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

public inline fun <reified Key, Value> koneReifiedMapOf(vararg entries: KoneMapEntry<Key, Value>): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(entries = entries, keyContext = defaultReifiedEquality())

public fun <Key, Value> koneReifiedMapOf(vararg entries: KoneMapEntry<Key, Value>, keyContext: ReifiedEquality<Key>): KoneReifiedMap<Key, Value> =
    when {
        entries.isEmpty() -> emptyKoneReifiedMap()
        keyContext is ReifiedHashing -> KoneHashResizableReifiedMap<Key, _, Value>(keyContext = keyContext).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedReifiedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

public fun <Key, Value> koneMutableMapOf(keyContext: Equality<Key> = defaultEquality()): KoneMutableMap<Key, Value> =
    if (keyContext is Hashing<Key>) KoneHashResizableMap(keyContext = keyContext)
    else KoneMutableListBackedMap(keyContext = keyContext)

public inline fun <reified Key, Value> koneMutableReifiedMapOf(): KoneMutableReifiedMap<Key, Value> =
    koneMutableReifiedMapOf(keyContext = defaultReifiedEquality())

public fun <Key, Value> koneMutableReifiedMapOf(keyContext: ReifiedEquality<Key>): KoneMutableReifiedMap<Key, Value> =
    if (keyContext is ReifiedHashing<Key>) KoneHashResizableReifiedMap(keyContext = keyContext)
    else KoneMutableListBackedReifiedMap(keyContext = keyContext)

public fun <Key, Value> koneMutableMapOf(vararg entries: KoneMapEntry<Key, Value>, keyContext: Equality<Key> = defaultEquality()): KoneMutableMap<Key, Value> =
    if (keyContext is Hashing<Key>) KoneHashResizableMap<Key, _, Value>(keyContext = keyContext).apply { setAllFrom(KoneArray(entries)) }
    else KoneMutableListBackedMap<Key, _, Value>(keyContext = keyContext).apply { setAllFrom(KoneArray(entries)) }

public inline fun <reified Key, Value> koneMutableReifiedMapOf(vararg entries: KoneMapEntry<Key, Value>): KoneMutableReifiedMap<Key, Value> =
    koneMutableReifiedMapOf(entries = entries, keyContext = defaultReifiedEquality())

public fun <Key, Value> koneMutableReifiedMapOf(vararg entries: KoneMapEntry<Key, Value>, keyContext: ReifiedEquality<Key>): KoneMutableReifiedMap<Key, Value> =
    if (keyContext is ReifiedHashing<Key>) KoneHashResizableReifiedMap<Key, _, Value>(keyContext = keyContext).apply { setAllFrom(KoneArray(entries)) }
    else KoneMutableListBackedReifiedMap<Key, _, Value>(keyContext = keyContext).apply { setAllFrom(KoneArray(entries)) }

public inline fun <Key, Value> buildKoneMap(
    keyContext: Equality<Key> = defaultEquality(),
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyContext is Hashing<Key>) KoneHashResizableMap<Key, _, Value>(keyContext = keyContext)
        else KoneMutableListBackedMap(keyContext = keyContext, KoneArrayResizableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

public inline fun <reified Key, Value> buildKoneReifiedMap(
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneReifiedMap(keyContext = defaultReifiedEquality(), builderAction)

public inline fun <Key, Value> buildKoneReifiedMap(
    keyContext: ReifiedEquality<Key>,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyContext is ReifiedHashing<Key>) KoneHashResizableReifiedMap<Key, _, Value>(keyContext = keyContext)
        else KoneMutableListBackedReifiedMap(keyContext = keyContext, KoneArrayResizableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

public inline fun <Key, Value> buildKoneMap(
    initialCapacity: UInt,
    keyContext: Equality<Key> = defaultEquality(),
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyContext is Hashing<Key>) KoneHashResizableMap<Key, _, Value>(keyContext = keyContext) // TODO: Replace with growable hash map
        else KoneMutableListBackedMap(initialCapacity = initialCapacity, keyContext = keyContext, KoneArrayGrowableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

public inline fun <reified Key, Value> buildKoneReifiedMap(
    initialCapacity: UInt,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneReifiedMap(initialCapacity = initialCapacity, keyContext = defaultReifiedEquality(), builderAction = builderAction)

public inline fun <Key, Value> buildKoneReifiedMap(
    initialCapacity: UInt,
    keyContext: ReifiedEquality<Key>,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyContext is ReifiedHashing<Key>) KoneHashResizableReifiedMap<Key, _, Value>(keyContext = keyContext) // TODO: Replace with growable hash map
        else KoneMutableListBackedReifiedMap(initialCapacity = initialCapacity, keyContext = keyContext, KoneArrayGrowableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

// TODO: Move following function somewhere else
public inline fun <Element, Key, Value, Destination: KoneMutableMap<in Key, in Value>> KoneIterable<Element>.associateTo(destination: Destination, transform: (Element) -> KoneMapEntry<Key, Value>): Destination {
    for (element in this) destination.set(transform(element))
    return destination
}

public inline fun <Element, Key, Destination : KoneMutableMap<in Key, in Element>> KoneIterable<Element>.associateByTo(destination: Destination, keySelector: (Element) -> Key): Destination {
    for (element in this) destination[keySelector(element)] = element
    return destination
}

public inline fun <Element, Key, Value, Destination : KoneMutableMap<in Key, in Value>> KoneIterable<Element>.associateByTo(destination: Destination, keySelector: (Element) -> Key, valueTransform: (Element) -> Value): Destination {
    for (element in this) destination[keySelector(element)] = valueTransform(element)
    return destination
}

public inline fun <Key, Value, Destination : KoneMutableMap<in Key, in Value>> KoneIterable<Key>.associateWithTo(destination: Destination, valueSelector: (Key) -> Value): Destination {
    for (element in this) destination[element] = valueSelector(element)
    return destination
}

public inline fun <Element, Key, Value> KoneIterable<Element>.associate(keyContext: Equality<Key> = defaultEquality(), transform: (Element) -> KoneMapEntry<Key, Value>): KoneMap<Key, Value> =
    associateTo(koneMutableMapOf(keyContext = keyContext), transform)

public inline fun <Element, reified Key, Value> KoneIterable<Element>.associateReified(transform: (Element) -> KoneMapEntry<Key, Value>): KoneReifiedMap<Key, Value> =
    associateReified(keyContext = defaultReifiedEquality(), transform = transform)

public inline fun <Element, Key, Value> KoneIterable<Element>.associateReified(keyContext: ReifiedEquality<Key>, transform: (Element) -> KoneMapEntry<Key, Value>): KoneReifiedMap<Key, Value> =
    associateTo(koneMutableReifiedMapOf(keyContext = keyContext), transform)

public inline fun <Element, Key> KoneIterable<Element>.associateBy(keyContext: Equality<Key> = defaultEquality(), keySelector: (Element) -> Key): KoneMap<Key, Element> =
    associateByTo(koneMutableMapOf(keyContext = keyContext), keySelector)

public inline fun <Element, reified Key> KoneIterable<Element>.associateByReified(keySelector: (Element) -> Key): KoneReifiedMap<Key, Element> =
    associateByReified(keyContext = defaultReifiedEquality(), keySelector = keySelector)

public inline fun <Element, Key> KoneIterable<Element>.associateByReified(keyContext: ReifiedEquality<Key>, keySelector: (Element) -> Key): KoneReifiedMap<Key, Element> =
    associateByTo(koneMutableReifiedMapOf(keyContext = keyContext), keySelector)

public inline fun <Element, Key, Value> KoneIterable<Element>.associateBy(keyContext: Equality<Key> = defaultEquality(), keySelector: (Element) -> Key, valueTransform: (Element) -> Value): KoneMap<Key, Value> =
    associateByTo(koneMutableMapOf(keyContext = keyContext), keySelector, valueTransform)

public inline fun <Element, reified Key, Value> KoneIterable<Element>.associateByReified(keySelector: (Element) -> Key, valueTransform: (Element) -> Value): KoneReifiedMap<Key, Value> =
    associateByReified(keyContext = defaultReifiedEquality(), keySelector = keySelector, valueTransform = valueTransform)

public inline fun <Element, Key, Value> KoneIterable<Element>.associateByReified(keyContext: ReifiedEquality<Key>, keySelector: (Element) -> Key, valueTransform: (Element) -> Value): KoneReifiedMap<Key, Value> =
    associateByTo(koneMutableReifiedMapOf(keyContext = keyContext), keySelector, valueTransform)

public inline fun <Key, Value> KoneIterable<Key>.associateWith(keyContext: Equality<Key> = defaultEquality(), valueSelector: (Key) -> Value): KoneMap<Key, Value> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext), valueSelector)

public inline fun <reified Key, Value> KoneIterable<Key>.associateWithReified(valueSelector: (Key) -> Value): KoneReifiedMap<Key, Value> =
    associateWithReified(keyContext = defaultReifiedEquality(), valueSelector = valueSelector)

public inline fun <Key, Value> KoneIterable<Key>.associateWithReified(keyContext: ReifiedEquality<Key>, valueSelector: (Key) -> Value): KoneReifiedMap<Key, Value> =
    associateWithTo(koneMutableReifiedMapOf(keyContext = keyContext), valueSelector)

public inline fun <K, V, W, D : KoneMutableMap<in K, in W>> KoneMap<out K, V>.mapValuesTo(destination: D, transform: (KoneMapEntry<K, V>) -> W): D =
    entriesView.associateByTo(destination, { it.key }, transform)

public inline fun <K, V, W> KoneMap<out K, V>.mapValues(keyContext: Equality<K> = defaultEquality(), transform: (KoneMapEntry<K, V>) -> W): KoneMap<K, W> =
    mapValuesTo(koneMutableMapOf<K, W>(keyContext = keyContext), transform)

public inline fun <K, V, W> KoneMap<out K, V>.mapValuesReified(keyContext: ReifiedEquality<K>, transform: (KoneMapEntry<K, V>) -> W): KoneReifiedMap<K, W> =
    mapValuesTo(koneMutableReifiedMapOf<K, W>(keyContext = keyContext), transform)

public inline fun <reified K, V, W> KoneMap<out K, V>.mapValuesReified(transform: (KoneMapEntry<K, V>) -> W): KoneReifiedMap<K, W> =
    mapValuesTo(koneMutableReifiedMapOf<K, W>(), transform)

public inline fun <K, V, L, D : KoneMutableMap<in L, in V>> KoneMap<out K, V>.mapKeysTo(destination: D, transform: (KoneMapEntry<K, V>) -> L): D =
    entriesView.associateByTo(destination, transform, { it.value })

public inline fun <K, V, L> KoneMap<out K, V>.mapKeys(keyContext: Equality<L> = defaultEquality(), transform: (KoneMapEntry<K, V>) -> L): KoneMap<L, V> =
    mapKeysTo(koneMutableMapOf(keyContext), transform)

public inline fun <K, V, L> KoneMap<out K, V>.mapKeysReified(keyContext: ReifiedEquality<L>, transform: (KoneMapEntry<K, V>) -> L): KoneReifiedMap<L, V> =
    mapKeysTo(koneMutableReifiedMapOf(keyContext), transform)

public inline fun <K, V, reified L> KoneMap<out K, V>.mapKeysReified(transform: (KoneMapEntry<K, V>) -> L): KoneReifiedMap<L, V> =
    mapKeysTo(koneMutableReifiedMapOf(), transform)

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneMap<out K, V>.filterKeysTo(destination: D, predicate: (K) -> Boolean): D {
    for ((key, value) in this) if (predicate(key)) destination[key] = value
    return destination
}

public inline fun <K, V> KoneMap<out K, V>.filterKeys(keyContext: Equality<K> = defaultEquality(), predicate: (K) -> Boolean): KoneMap<K, V> =
    filterKeysTo(koneMutableMapOf(keyContext), predicate)

public inline fun <reified K, V> KoneMap<out K, V>.filterKeysReified(predicate: (K) -> Boolean): KoneReifiedMap<K, V> =
    filterKeysTo(koneMutableReifiedMapOf(defaultReifiedEquality()), predicate)

public inline fun <K, V> KoneMap<out K, V>.filterKeysReified(keyContext: ReifiedEquality<K>, predicate: (K) -> Boolean): KoneReifiedMap<K, V> =
    filterKeysTo(koneMutableReifiedMapOf(keyContext), predicate)

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneMap<out K, V>.filterValuesTo(destination: D, predicate: (V) -> Boolean): D {
    for ((key, value) in this) if (predicate(value)) destination[key] = value
    return destination
}

public inline fun <K, V> KoneMap<out K, V>.filterValues(keyContext: Equality<K> = defaultEquality(), predicate: (V) -> Boolean): KoneMap<K, V> =
    filterValuesTo(koneMutableMapOf(keyContext), predicate)

public inline fun <reified K, V> KoneMap<out K, V>.filterValuesReified(predicate: (V) -> Boolean): KoneReifiedMap<K, V> =
    filterValuesTo(koneMutableReifiedMapOf(defaultReifiedEquality()), predicate)

public inline fun <K, V> KoneMap<out K, V>.filterValuesReified(keyContext: ReifiedEquality<K>, predicate: (V) -> Boolean): KoneReifiedMap<K, V> =
    filterValuesTo(koneMutableReifiedMapOf(keyContext), predicate)