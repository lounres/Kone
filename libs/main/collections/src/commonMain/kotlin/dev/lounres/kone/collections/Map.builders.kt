/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalTypeInference::class)

package dev.lounres.kone.collections

import dev.lounres.kone.collections.implementations.KoneArrayResizableLinkedNoddedListProducer
import dev.lounres.kone.collections.implementations.KoneEmptyMap
import dev.lounres.kone.collections.implementations.KoneMutableListBackedMap
import dev.lounres.kone.collections.implementations.KoneHashResizableMap
import dev.lounres.kone.collections.implementations.KoneSingletonMap
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.defaultEquality
import kotlin.contracts.InvocationKind
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders with vararg map nodes

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> emptyKoneMap(): KoneMap<Key, Value> = KoneEmptyMap as KoneMap<Key, Value>

@Suppress("unused")
public fun <Key, Value> koneMapOf(keyContext: Equality<Key> = defaultEquality()): KoneMap<Key, Value> =
    emptyKoneMap()

public fun <Key, Value> koneMapOf(entry: KoneMapEntry<Key, Value>, keyContext: Equality<Key> = defaultEquality()): KoneMap<Key, Value> =
    KoneSingletonMap(
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


public fun <Key, Value> koneMutableMapOf(keyContext: Equality<Key> = defaultEquality()): KoneMutableMap<Key, Value> =
    if (keyContext is Hashing<Key>) KoneHashResizableMap(keyContext = keyContext)
    else KoneMutableListBackedMap(keyContext = keyContext)

public fun <Key, Value> koneMutableMapOf(vararg entries: KoneMapEntry<Key, Value>, keyContext: Equality<Key> = defaultEquality()): KoneMutableMap<Key, Value> =
    if (keyContext is Hashing<Key>) KoneHashResizableMap<Key, _, Value>(keyContext = keyContext).apply { setAllFrom(KoneArray(entries)) }
    else KoneMutableListBackedMap<Key, _, Value>(keyContext = keyContext).apply { setAllFrom(KoneArray(entries)) }

public inline fun <Key, Value> buildKoneMap(
    keyContext: Equality<Key> = defaultEquality(),
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyContext is Hashing<Key>) KoneHashResizableMap<Key, _, Value>(keyContext = keyContext)
        else KoneMutableListBackedMap(keyContext = keyContext, KoneArrayResizableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

public inline fun <Key, Value> buildKoneMap(
    initialCapacity: UInt,
    keyContext: Equality<Key> = defaultEquality(),
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyContext is Hashing<Key>) KoneHashResizableMap<Key, _, Value>(keyContext = keyContext) // TODO: Replace with growable hash map
        else KoneMutableListBackedMap(keyContext = keyContext/*, KoneArrayGrowableLinkedNoddedListProducer*/) // TODO: Enable producer and use `initialCapacity`
    return mapBuilder.apply(builderAction)
}

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

public inline fun <Element, Key> KoneIterable<Element>.associateBy(keyContext: Equality<Key> = defaultEquality(), keySelector: (Element) -> Key): KoneMap<Key, Element> =
    associateByTo(koneMutableMapOf(keyContext = keyContext), keySelector)

public inline fun <Element, Key, Value> KoneIterable<Element>.associateBy(keyContext: Equality<Key> = defaultEquality(), keySelector: (Element) -> Key, valueTransform: (Element) -> Value): KoneMap<Key, Value> =
    associateByTo(koneMutableMapOf(keyContext = keyContext), keySelector, valueTransform)

public inline fun <Key, Value> KoneIterable<Key>.associateWith(keyContext: Equality<Key> = defaultEquality(), valueSelector: (Key) -> Value): KoneMap<Key, Value> =
    associateWithTo(koneMutableMapOf(keyContext = keyContext), valueSelector)