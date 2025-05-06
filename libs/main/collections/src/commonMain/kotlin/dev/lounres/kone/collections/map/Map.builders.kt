/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

@file:OptIn(ExperimentalTypeInference::class)

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
import dev.lounres.kone.collections.array.DelicateImmutableArrayConstructor
import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.iterables.KoneIterable
import dev.lounres.kone.collections.iterables.next
import dev.lounres.kone.collections.list.implementations.KoneArrayGrowableLinkedNoddedListProducer
import dev.lounres.kone.collections.list.implementations.KoneArrayResizableLinkedNoddedListProducer
import dev.lounres.kone.collections.map.empty.KoneEmptyReifiedMap
import dev.lounres.kone.collections.map.implementations.KoneHashResizableMap
import dev.lounres.kone.collections.map.implementations.KoneHashResizableReifiedMap
import dev.lounres.kone.collections.map.implementations.KoneListBackedMutableMap
import dev.lounres.kone.collections.map.implementations.KoneListBackedMutableReifiedMap
import dev.lounres.kone.collections.map.singleton.KoneSingletonMap
import dev.lounres.kone.collections.map.singleton.KoneSingletonReifiedMap
import dev.lounres.kone.collections.set.KoneReifiedSet
import dev.lounres.kone.collections.set.KoneSet
import dev.lounres.kone.contexts.KoneContextRegistry
import dev.lounres.kone.relations.*
import dev.lounres.kone.suppliedTypes.SuppliedType
import kotlin.contracts.InvocationKind
import kotlin.experimental.ExperimentalTypeInference
import kotlin.jvm.JvmName


// TODO: Add builders with vararg map nodes

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> KoneMap.Companion.empty(): KoneMap<Key, Value> = KoneEmptyReifiedMap as KoneMap<Key, Value>

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> KoneReifiedMap.Companion.empty(): KoneReifiedMap<Key, Value> = KoneEmptyReifiedMap as KoneReifiedMap<Key, Value>

@Suppress("unused")
public fun <Key, Value> KoneMap.Companion.of(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> = KoneMap.empty()

@Suppress("unused")
context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneMap.Companion.contextualOf(
    keyType: SuppliedType<Key>,
): KoneMap<Key, Value> = KoneMap.empty()

@Suppress("unused")
public inline fun <reified Key, Value> KoneReifiedMap.Companion.of(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = KoneReifiedMap.empty()

@Suppress("unused")
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.of<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.of"))
public fun <Key, Value> koneReifiedMapOf(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = KoneReifiedMap.empty()

@Suppress("unused")
public fun <Key, Value> KoneReifiedMap.Companion.of(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = KoneReifiedMap.empty()

context(koneContextRegistry: KoneContextRegistry)
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.contextualOf<Key, Value>(keyType = keyType)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.contextualOf"))
public fun <Key, Value> koneContextualReifiedMapOf(
    keyType: SuppliedType<Key>,
): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneReifiedMap.Companion.contextualOf(
    keyType: SuppliedType<Key>,
): KoneReifiedMap<Key, Value> =
    koneReifiedMapOf(
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Suppress("unused")
@Deprecated("", replaceWith = ReplaceWith("KoneMap.of<Key, Value>(entry, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.of"))
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

@Suppress("unused")
public fun <Key, Value> KoneMap.Companion.of(
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

@Deprecated("", replaceWith = ReplaceWith("KoneMap.contextualOf<Key, Value>(entry, keyType = keyType)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.contextualOf"))
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

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneMap.Companion.contextualOf(
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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.of<Key, Value>(entry, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.of"))
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
public inline fun <reified Key, Value> KoneReifiedMap.Companion.of(
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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.of<Key, Value>(entry, keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.of"))
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

@Suppress("unused")
public fun <Key, Value> KoneReifiedMap.Companion.of(
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
@Deprecated("", replaceWith = ReplaceWith("KoneMap.of<Key, Value>(*entries, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.of"))
public fun <Key, Value> koneMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> =
    when {
        entries.isEmpty() -> KoneMap.empty()
        keyHashing != null -> KoneHashResizableMap<Key, Value>(keyEquality = keyEquality).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> KoneMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> =
    when {
        entries.isEmpty() -> KoneMap.empty()
        keyHashing != null -> KoneHashResizableMap<Key, Value>(keyEquality = keyEquality).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

@Deprecated("", replaceWith = ReplaceWith("KoneMap.contextualOf<Key, Value>(*entries, keyType = keyType)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.contextualOf"))
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

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneMap.Companion.contextualOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyType: SuppliedType<Key>,
): KoneMap<Key, Value> =
    koneMapOf(
        entries = entries,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.of<Key, Value>(*entries, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.of"))
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

public inline fun <reified Key, Value> KoneReifiedMap.Companion.of(
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
@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.of<Key, Value>(*entries, keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.of"))
public fun <Key, Value> koneReifiedMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    when {
        entries.isEmpty() -> KoneReifiedMap.empty()
        keyHashing != null -> KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedReifiedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> KoneReifiedMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    when {
        entries.isEmpty() -> KoneReifiedMap.empty()
        keyHashing != null -> KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing).apply {
            setAllFrom(KoneArray(entries))
        }
//        else -> KoneMutableListBackedReifiedMap(keyContext = keyContext).apply {
//            setAllFrom(KoneArray(entries))
//        }
        else -> TODO()
    }

@Deprecated("", replaceWith = ReplaceWith("KoneMutableMap.of<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMutableMap", "dev.lounres.kone.collections.map.of"))
public fun <Key, Value> koneMutableMapOf(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap(keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableMap(keyEquality = keyEquality)

public fun <Key, Value> KoneMutableMap.Companion.of(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap(keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableMap(keyEquality = keyEquality)

@Deprecated("", replaceWith = ReplaceWith("KoneMutableMap.contextualOf<Key, Value>(keyType = keyType)", "dev.lounres.kone.collections.map.KoneMutableMap", "dev.lounres.kone.collections.map.contextualOf"))
context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> koneContextualMutableMapOf(
    keyType: SuppliedType<Key>,
): KoneMutableMap<Key, Value> =
    koneMutableMapOf(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneMutableMap.Companion.contextualOf(
    keyType: SuppliedType<Key>,
): KoneMutableMap<Key, Value> =
    koneMutableMapOf(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedMap.of<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMutableReifiedMap", "dev.lounres.kone.collections.map.of"))
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

public inline fun <reified Key, Value> KoneMutableReifiedMap.Companion.of(
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

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedMap.of<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMutableReifiedMap", "dev.lounres.kone.collections.map.of"))
public fun <Key, Value> koneMutableReifiedMapOf(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality)

public fun <Key, Value> KoneMutableReifiedMap.Companion.of(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality)

@Deprecated("", replaceWith = ReplaceWith("KoneMutableMap.of<Key, Value>(*entries, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMutableMap", "dev.lounres.kone.collections.map.of"))
@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> koneMutableMapOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableMap<Key, Value>(keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> KoneMutableMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableMap<Key, Value>(keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

@Deprecated("", replaceWith = ReplaceWith("KoneMutableMap.contextualOf<Key, Value>(*entries, keyType = keyType)", "dev.lounres.kone.collections.map.KoneMutableMap", "dev.lounres.kone.collections.map.contextualOf"))
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

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneMutableMap.Companion.contextualOf(
    vararg entries: KoneMapEntry<Key, Value>,
    keyType: SuppliedType<Key>,
): KoneMutableMap<Key, Value> =
    koneMutableMapOf(
        entries = entries,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedMap.of<Key, Value>(*entries, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMutableReifiedMap", "dev.lounres.kone.collections.map.of"))
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

public inline fun <reified Key, Value> KoneMutableReifiedMap.Companion.of(
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

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedMap.of<Key, Value>(*entries, keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder)", "dev.lounres.kone.collections.map.KoneMutableReifiedMap", "dev.lounres.kone.collections.map.of"))
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

@OptIn(DelicateImmutableArrayConstructor::class)
public fun <Key, Value> KoneMutableReifiedMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

@Deprecated("", replaceWith = ReplaceWith("KoneMutableReifiedMap.contextualOf<Key, Value>(*entries, keyType = keyType)", "dev.lounres.kone.collections.map.KoneMutableReifiedMap", "dev.lounres.kone.collections.map.contextualOf"))
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

context(koneContextRegistry: KoneContextRegistry)
public fun <Key, Value> KoneMutableReifiedMap.Companion.contextualOf(
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

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneMapBuilder<Key, Value> @PublishedApi internal constructor(result: KoneMutableMap<Key, Value>) : KoneMutableMap<Key, Value> {
    private var result: KoneMutableMap<Key, Value>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.size
    }
    
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.nodesView
    }
    
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.nodes
    }
    
    override val keysView: KoneSet<Key> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.keysView
    }
    
    override val keys: KoneSet<Key> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.keys
    }
    
    override val valuesView: KoneIterable<Value> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.valuesView
    }
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.getNodeOrNull(key)
    }
    
    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.set(key, value)
    }
    
    override fun remove(key: Key) {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.remove(key)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Key, Value) -> Boolean) {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllNodesThat(predicate: (KoneMutableMapNode<Key, Value>) -> Boolean) {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.removeAllNodesThat(predicate)
    }
    
    public operator fun KoneMapEntry<Key, Value>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.set(this)
    }
    
    public operator fun KoneIterable<KoneMapEntry<Key, Value>>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.setAllFrom(this)
    }
    
    public operator fun KoneMap<out Key, Value>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.setAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneMap<Key, Value> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneReifiedMapBuilder<Key, Value> @PublishedApi internal constructor(result: KoneMutableReifiedMap<Key, Value>) : KoneMutableReifiedMap<Key, Value> {
    private var result: KoneMutableReifiedMap<Key, Value>? = result
    
    override val size: UInt get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.size
    }
    
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.nodesView
    }
    
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.nodes
    }
    
    override val keysView: KoneReifiedSet<Key> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.keysView
    }
    
    override val keys: KoneReifiedSet<Key> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.keys
    }
    
    override val valuesView: KoneIterable<Value> get() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.valuesView
    }
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.getNodeOrNull(key)
    }
    
    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        return result.set(key, value)
    }
    
    override fun remove(key: Key) {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.remove(key)
    }
    
    override fun removeAll() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Key, Value) -> Boolean) {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllNodesThat(predicate: (KoneMutableMapNode<Key, Value>) -> Boolean) {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.removeAllNodesThat(predicate)
    }
    
    public operator fun KoneMapEntry<Key, Value>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.set(this)
    }
    
    public operator fun KoneIterable<KoneMapEntry<Key, Value>>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.setAllFrom(this)
    }
    
    public operator fun KoneMap<out Key, Value>.unaryPlus() {
        val result = result
        if (result == null) error("This KoneMap builder is already used")
        result.setAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneReifiedMap<Key, Value> {
        val result = result
        if (result == null) error("This KoneList builder is already used")
        return result.also { this.result = null }
    }
}

@Deprecated("", replaceWith = ReplaceWith("KoneMap.build(keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder, builderAction)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.build"))
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

@Deprecated("")
@JvmName("buildOld")
public inline fun <Key, Value> KoneMap.Companion.build(
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

public inline fun <Key, Value> KoneMap.Companion.build(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMapBuilder<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing)
        else KoneListBackedMutableMap(keyEquality = keyEquality, KoneArrayResizableLinkedNoddedListProducer)
    return KoneMapBuilder(mapBuilder).apply(builderAction).build()
}

@Deprecated("", replaceWith = ReplaceWith("KoneMap.buildContextual(keyType = keyType, builderAction)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.buildContextual"))
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

@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneMap.Companion.buildContextual(
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMutableMap<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    buildKoneMap(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction,
    )

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneMap.Companion.buildContextual(
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMapBuilder<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneMap.build(
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction
    )

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.build(keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder, builderAction)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.build"))
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

@Deprecated("")
@JvmName("buildOld")
public inline fun <reified Key, Value> KoneReifiedMap.Companion.build(
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

public inline fun <reified Key, Value> KoneReifiedMap.Companion.build(
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneReifiedMap.build<Key, Value>(
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        builderAction
    )

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.build(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder, builderAction)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.build"))
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

@Deprecated("")
@JvmName("buildOld")
public inline fun <Key, Value> KoneReifiedMap.Companion.build(
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

public inline fun <Key, Value> KoneReifiedMap.Companion.build(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
        else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, KoneArrayResizableLinkedNoddedListProducer)
    return KoneReifiedMapBuilder(mapBuilder).apply(builderAction).build()
}

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.buildContextual(keyType = keyType, builderAction)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.buildContextual"))
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

@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneReifiedMap.Companion.buildContextual(
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

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneReifiedMap.Companion.buildContextual(
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneReifiedMap.build(
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction
    )

@Deprecated("", replaceWith = ReplaceWith("KoneMap.build(initialCapacity = initialCapacity, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder, builderAction)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.build"))
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

@Deprecated("")
@JvmName("buildOld")
public inline fun <Key, Value> KoneMap.Companion.build(
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

public inline fun <Key, Value> KoneMap.Companion.build(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneMapBuilder<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing) // TODO: Replace with growable hash map
        else KoneListBackedMutableMap(initialCapacity = initialCapacity, keyEquality = keyEquality, KoneArrayGrowableLinkedNoddedListProducer)
    return KoneMapBuilder(mapBuilder).apply(builderAction).build()
}

@Deprecated("", replaceWith = ReplaceWith("KoneMap.buildContextual(initialCapacity = initialCapacity, keyType = keyType, builderAction)", "dev.lounres.kone.collections.map.KoneMap", "dev.lounres.kone.collections.map.buildContextual"))
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

@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneMap.Companion.buildContextual(
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

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneMap.Companion.buildContextual(
    initialCapacity: UInt,
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneMapBuilder<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneMap.build(
        initialCapacity = initialCapacity,
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction
    )

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.build(initialCapacity = initialCapacity, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder, builderAction)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.build"))
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

@Deprecated("")
@JvmName("buildOld")
public inline fun <reified Key, Value> KoneReifiedMap.Companion.build(
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

public inline fun <reified Key, Value> KoneReifiedMap.Companion.build(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneReifiedMap.build<Key, Value>(
        initialCapacity = initialCapacity,
        keyReification = Reification(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        builderAction
    )

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.build(initialCapacity = initialCapacity, keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing, keyOrder = keyOrder, builderAction)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.build"))
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

@Deprecated("")
@JvmName("buildOld")
public inline fun <Key, Value> KoneReifiedMap.Companion.build(
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

public inline fun <Key, Value> KoneReifiedMap.Companion.build(
    initialCapacity: UInt,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = defaultEquality(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    @BuilderInference builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing) // TODO: Replace with growable hash map
        else KoneListBackedMutableReifiedMap(initialCapacity = initialCapacity, keyReification = keyReification, keyEquality = keyEquality, KoneArrayGrowableLinkedNoddedListProducer)
    return KoneReifiedMapBuilder(mapBuilder).apply(builderAction).build()
}

@Deprecated("", replaceWith = ReplaceWith("KoneReifiedMap.buildContextual(initialCapacity = initialCapacity, keyType = keyType, builderAction)", "dev.lounres.kone.collections.map.KoneReifiedMap", "dev.lounres.kone.collections.map.buildContextual"))
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

@Deprecated("")
@JvmName("buildContextualOld")
context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneReifiedMap.Companion.buildContextual(
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

context(koneContextRegistry: KoneContextRegistry)
public inline fun <Key, Value> KoneReifiedMap.Companion.buildContextual(
    initialCapacity: UInt,
    keyType: SuppliedType<Key>,
    @BuilderInference builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneReifiedMap.build(
        initialCapacity = initialCapacity,
        keyReification = koneContextRegistry.loadReificationFor(keyType),
        keyEquality = koneContextRegistry.loadEqualityFor(keyType),
        keyHashing = koneContextRegistry.loadHashingForOrNull(keyType),
        keyOrder = koneContextRegistry.loadOrderForOrNull(keyType),
        builderAction = builderAction
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
    nodesView.associateByTo(destination, { it.key }, transform)

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
    nodesView.associateByTo(destination = destination, keySelector = transform, valueTransform = { it.value })

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
    for (entry in this) if (predicate(entry.key)) destination[entry.key] = entry.value
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
    for (entry in this) if (predicate(entry.value)) destination[entry.key] = entry.value
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