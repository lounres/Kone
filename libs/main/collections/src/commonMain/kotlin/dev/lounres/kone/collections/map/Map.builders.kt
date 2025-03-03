/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalTypeInference::class)

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.array.DelicateImmutableArrayConstructor
import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableLinkedNoddedListProducer
import dev.lounres.kone.collections.map.empty.KoneEmptyReifiedMap
import dev.lounres.kone.collections.map.implementations.KoneListBackedMutableMap
import dev.lounres.kone.collections.map.implementations.KoneHashResizableMap
import dev.lounres.kone.collections.map.implementations.KoneHashResizableReifiedMap
import dev.lounres.kone.collections.map.implementations.KoneListBackedMutableReifiedMap
import dev.lounres.kone.collections.map.singleton.KoneSingletonMap
import dev.lounres.kone.collections.map.singleton.KoneSingletonReifiedMap
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedListProducer
import dev.lounres.kone.comparison.Equality
import dev.lounres.kone.comparison.Hashing
import dev.lounres.kone.comparison.Order
import dev.lounres.kone.comparison.Reification
import dev.lounres.kone.comparison.defaultEquality
import dev.lounres.kone.comparison.loadEqualityFor
import dev.lounres.kone.comparison.loadHashingForOrNull
import dev.lounres.kone.comparison.loadOrderForOrNull
import dev.lounres.kone.comparison.loadReificationFor
import dev.lounres.kone.context.KoneContextRegistry
import dev.lounres.kone.util.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.experimental.ExperimentalTypeInference


// TODO: Add builders with vararg map nodes

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> emptyKoneMap(): KoneMap<Key, Value> = KoneEmptyReifiedMap as KoneMap<Key, Value>

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> emptyKoneReifiedMap(): KoneReifiedMap<Key, Value> = KoneEmptyReifiedMap as KoneReifiedMap<Key, Value>

@Suppress("unused")
public fun <Key, Value> koneMapOf(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> = emptyKoneMap()

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMapOf(
    keyType: SuppliedType<Key>,
): KoneMap<Key, Value> =
    koneMapOf(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Suppress("unused")
public inline fun <reified Key, Value> koneReifiedMapOf(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = emptyKoneReifiedMap()

@Suppress("unused")
public fun <Key, Value> koneReifiedMapOf(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = emptyKoneReifiedMap()

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualReifiedMapOf(
    keyType: SuppliedType<Key>,
): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Suppress("unused")
public fun <Key, Value> koneMapOf(
    entry: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> =
    KoneSingletonMap(
        singleKey = entry.key,
        singleValue = entry.value,
        keyEquality = keyEquality,
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMapOf(
    entry: KoneMapEntry<Key, Value>,
    keyType: SuppliedType<Key>,
): KoneMap<Key, Value> =
    koneMapOf(
        entry = entry,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Suppress("unused")
public inline fun <reified Key, Value> koneReifiedMapOf(
    entry: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(
        entry = entry,
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

@Suppress("unused")
public fun <Key, Value> koneReifiedMapOf(
    entry: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    KoneSingletonReifiedMap(
        singleKey = entry.key,
        singleValue = entry.value,
        keyReification = keyReification,
        keyEquality = keyEquality,
    )

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> koneMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> =
    when {
        entries.isEmpty() -> emptyKoneMap()
        keyHashing != null -> KoneHashResizableMap<Key, Value>(keyEquality = keyEquality).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyType: SuppliedType<Key>,
): KoneMap<Key, Value> =
    koneMapOf(
        entries = entries,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

public inline fun <reified Key, Value> koneReifiedMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(
        entries = entries,
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> koneReifiedMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    when {
        entries.isEmpty() -> emptyKoneReifiedMap()
        keyHashing != null -> KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedReifiedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

public fun <Key, Value> koneMutableMapOf(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap(keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableMap(keyEquality = keyEquality)

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMutableMapOf(
    keyType: SuppliedType<Key>,
): KoneMutableMap<Key, Value> =
    koneMutableMapOf(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

public inline fun <reified Key, Value> koneMutableReifiedMapOf(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    koneMutableReifiedMapOf(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

public fun <Key, Value> koneMutableReifiedMapOf(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality)

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> koneMutableMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableMap<Key, Value>(keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMutableMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyType: SuppliedType<Key>,
): KoneMutableMap<Key, Value> =
    koneMutableMapOf(
        entries = entries,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

public inline fun <reified Key, Value> koneMutableReifiedMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    koneMutableReifiedMapOf(
        entries = entries,
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> koneMutableReifiedMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMutableReifiedMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyType: SuppliedType<Key>,
): KoneMutableReifiedMap<Key, Value> =
    koneMutableReifiedMapOf(
        entries = entries,
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

public inline fun <Key, Value> buildKoneMap(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing)
        else KoneListBackedMutableMap(keyEquality = keyEquality, KoneArrayResizableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> buildKoneContextualMap(
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneMap(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction,
    )

public inline fun <reified Key, Value> buildKoneReifiedMap(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneReifiedMap(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        builderAction = builderAction
    )

public inline fun <Key, Value> buildKoneReifiedMap(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
        else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, KoneArrayResizableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> buildKoneContextualReifiedMap(
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneReifiedMap(
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction,
    )

public inline fun <Key, Value> buildKoneMap(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing) // TODO: Replace with growable hash map
        else KoneListBackedMutableMap(initialCapacity = initialCapacity, keyEquality = keyEquality, KoneArrayGrowableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> buildKoneContextualMap(
    initialCapacity: UInt,
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneMap(
        initialCapacity = initialCapacity,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction,
    )

public inline fun <reified Key, Value> buildKoneReifiedMap(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneReifiedMap(
        initialCapacity = initialCapacity,
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        builderAction = builderAction,
    )

public inline fun <Key, Value> buildKoneReifiedMap(
    initialCapacity: UInt,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing) // TODO: Replace with growable hash map
        else KoneListBackedMutableReifiedMap(initialCapacity = initialCapacity, keyReification = keyReification, keyEquality = keyEquality, KoneArrayGrowableLinkedNoddedListProducer)
    return mapBuilder.apply(builderAction)
}

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> buildKoneContextualReifiedMap(
    initialCapacity: UInt,
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMutableReifiedMap<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneReifiedMap(
        initialCapacity = initialCapacity,
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction,
    )

// TODO: Move the following functions somewhere else
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

public inline fun <Element, Key, Value> KoneIterable<Element>.associate(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (Element) -> KoneMapEntry<Key, Value>
): KoneMap<Key, Value> =
    associateTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder
        ),
        transform = transform,
    )

public inline fun <Element, reified Key, Value> KoneIterable<Element>.associateReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (Element) -> KoneMapEntry<Key, Value>,
): KoneReifiedMap<Key, Value> =
    associateReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        transform = transform
    )

public inline fun <Element, Key, Value> KoneIterable<Element>.associateReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (Element) -> KoneMapEntry<Key, Value>
): KoneReifiedMap<Key, Value> =
    associateTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder
        ),
        transform = transform,
    )

public inline fun <Element, Key> KoneIterable<Element>.associateBy(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key
): KoneMap<Key, Element> =
    associateByTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
    )

public inline fun <Element, reified Key> KoneIterable<Element>.associateByReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key
): KoneReifiedMap<Key, Element> =
    associateByReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        keySelector = keySelector
    )

public inline fun <Element, Key> KoneIterable<Element>.associateByReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
): KoneReifiedMap<Key, Element> =
    associateByTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
    )

public inline fun <Element, Key, Value> KoneIterable<Element>.associateBy(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
    valueTransform: (Element) -> Value
): KoneMap<Key, Value> =
    associateByTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

public inline fun <Element, reified Key, Value> KoneIterable<Element>.associateByReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
    valueTransform: (Element) -> Value
): KoneReifiedMap<Key, Value> =
    associateByReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        keySelector = keySelector,
        valueTransform = valueTransform
    )

public inline fun <Element, Key, Value> KoneIterable<Element>.associateByReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
    valueTransform: (Element) -> Value
): KoneReifiedMap<Key, Value> =
    associateByTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

public inline fun <Key, Value> KoneIterable<Key>.associateWith(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    valueSelector: (Key) -> Value
): KoneMap<Key, Value> =
    associateWithTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        valueSelector = valueSelector,
    )

public inline fun <reified Key, Value> KoneIterable<Key>.associateWithReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    valueSelector: (Key) -> Value,
): KoneReifiedMap<Key, Value> =
    associateWithReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        valueSelector = valueSelector,
    )

public inline fun <Key, Value> KoneIterable<Key>.associateWithReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    valueSelector: (Key) -> Value
): KoneReifiedMap<Key, Value> =
    associateWithTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        valueSelector = valueSelector,
    )

public inline fun <K, V, W, D : KoneMutableMap<in K, in W>> KoneMap<out K, V>.mapValuesTo(destination: D, transform: (KoneMapEntry<K, V>) -> W): D =
    entriesView.associateByTo(destination, { it.key }, transform)

public inline fun <Key, V, W> KoneMap<out Key, V>.mapValues(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (KoneMapEntry<Key, V>) -> W
): KoneMap<Key, W> =
    mapValuesTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        transform = transform,
    )

public inline fun <reified Key, V, W> KoneMap<out Key, V>.mapValuesReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (KoneMapEntry<Key, V>) -> W
): KoneReifiedMap<Key, W> =
    mapValuesReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        transform = transform,
    )

public inline fun <Key, V, W> KoneMap<out Key, V>.mapValuesReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (KoneMapEntry<Key, V>) -> W
): KoneReifiedMap<Key, W> =
    mapValuesTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        transform = transform,
    )

public inline fun <K, V, L, D : KoneMutableMap<in L, in V>> KoneMap<out K, V>.mapKeysTo(destination: D, transform: (KoneMapEntry<K, V>) -> L): D =
    entriesView.associateByTo(destination = destination, keySelector = transform, valueTransform = { it.value })

public inline fun <K, V, L> KoneMap<out K, V>.mapKeys(
    keyEquality: Equality<L> = defaultEquality(),
    keyHashing: Hashing<L>? = null,
    keyOrder: Order<L>? = null,
    transform: (KoneMapEntry<K, V>) -> L
): KoneMap<L, V> =
    mapKeysTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        transform = transform,
    )

public inline fun <K, V, reified L> KoneMap<out K, V>.mapKeysReified(
    keyEquality: Equality<L> = defaultEquality(),
    keyHashing: Hashing<L>? = null,
    keyOrder: Order<L>? = null,
    transform: (KoneMapEntry<K, V>) -> L
): KoneReifiedMap<L, V> =
    mapKeysReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        transform = transform,
    )


public inline fun <K, V, L> KoneMap<out K, V>.mapKeysReified(
    keyReification: Reification<L>,
    keyEquality: Equality<L> = defaultEquality(),
    keyHashing: Hashing<L>? = null,
    keyOrder: Order<L>? = null,
    transform: (KoneMapEntry<K, V>) -> L
): KoneReifiedMap<L, V> =
    mapKeysTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        transform = transform,
    )

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneMap<out K, V>.filterKeysTo(destination: D, predicate: (K) -> Boolean): D {
    for ((key, value) in this) if (predicate(key)) destination[key] = value
    return destination
}

public inline fun <Key, V> KoneMap<out Key, V>.filterKeys(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Key) -> Boolean
): KoneMap<Key, V> =
    filterKeysTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate,
    )

public inline fun <reified Key, V> KoneMap<out Key, V>.filterKeysReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Key) -> Boolean
): KoneReifiedMap<Key, V> =
    filterKeysReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        predicate = predicate,
    )

public inline fun <Key, V> KoneMap<out Key, V>.filterKeysReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Key) -> Boolean
): KoneReifiedMap<Key, V> =
    filterKeysTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate,
    )

public inline fun <K, V, D : KoneMutableMap<in K, in V>> KoneMap<out K, V>.filterValuesTo(destination: D, predicate: (V) -> Boolean): D {
    for ((key, value) in this) if (predicate(value)) destination[key] = value
    return destination
}

public inline fun <Key, V> KoneMap<out Key, V>.filterValues(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (V) -> Boolean
): KoneMap<Key, V> =
    filterValuesTo(
        koneMutableMapOf(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate,
    )

public inline fun <reified Key, V> KoneMap<out Key, V>.filterValuesReified(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (V) -> Boolean
): KoneReifiedMap<Key, V> =
    filterValuesReified(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        predicate = predicate,
    )

public inline fun <Key, Value> KoneMap<out Key, Value>.filterValuesReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Value) -> Boolean)
: KoneReifiedMap<Key, Value> =
    filterValuesTo(
        koneMutableReifiedMapOf(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate
    )