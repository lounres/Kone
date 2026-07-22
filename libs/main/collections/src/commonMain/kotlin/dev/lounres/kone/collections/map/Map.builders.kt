/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.DelicateCollectionsInheritanceAPI
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
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import kotlin.contracts.InvocationKind


// TODO: Add builders with vararg map nodes

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> KoneMap.Companion.empty(): KoneMap<Key, Value> = KoneEmptyReifiedMap as KoneMap<Key, Value>

@Suppress("UNCHECKED_CAST")
public fun <Key, Value> KoneReifiedMap.Companion.empty(): KoneReifiedMap<Key, Value> = KoneEmptyReifiedMap as KoneReifiedMap<Key, Value>

@Suppress("unused")
public fun <Key, Value> KoneMap.Companion.of(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> = KoneMap.empty()

// FIXME: Wait for KT-73135
//@Suppress("unused")
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneMap.Companion.contextualOf(): KoneMap<Key, Value> = KoneMap.empty()

@Suppress("unused")
public inline fun <reified Key, Value> KoneReifiedMap.Companion.of(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = KoneReifiedMap.empty()

@Suppress("unused")
public fun <Key, Value> KoneReifiedMap.Companion.of(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> = KoneReifiedMap.empty()

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneReifiedMap.Companion.contextualOf(): KoneReifiedMap<Key, Value> =
//    KoneReifiedMap.of(
//        keyReification = Reification.getFor(),
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//    )

@Suppress("unused")
public fun <Key, Value> KoneMap.Companion.of(
    entry: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMap<Key, Value> =
    KoneSingletonMap(
        singleKey = entry.key,
        singleValue = entry.value,
        keyEquality = keyEquality,
    )

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneMap.Companion.contextualOf(
//    entry: KoneMapEntry<Key, Value>,
//): KoneMap<Key, Value> =
//    KoneMap.of(
//        entry = entry,
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//    )

@Suppress("unused")
public inline fun <reified Key, Value> KoneReifiedMap.Companion.of(
    entry: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    KoneReifiedMap.of(
        entry = entry,
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

@Suppress("unused")
public fun <Key, Value> KoneReifiedMap.Companion.of(
    entry: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    KoneSingletonReifiedMap(
        singleKey = entry.key,
        singleValue = entry.value,
        keyReification = keyReification,
        keyEquality = keyEquality,
    )

public fun <Key, Value> KoneMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
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

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneMap.Companion.contextualOf(
//    vararg entries: KoneMapEntry<Key, Value>,
//): KoneMap<Key, Value> =
//    KoneMap.of(
//        entries = entries,
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//    )

public inline fun <reified Key, Value> KoneReifiedMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneReifiedMap<Key, Value> =
    KoneReifiedMap.of(
        entries = entries,
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

public fun <Key, Value> KoneReifiedMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
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

public fun <Key, Value> KoneMutableMap.Companion.of(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap(keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableMap(keyEquality = keyEquality)

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneMutableMap.Companion.contextualOf(): KoneMutableMap<Key, Value> =
//    KoneMutableMap.of(
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//    )

public inline fun <reified Key, Value> KoneMutableReifiedMap.Companion.of(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    KoneMutableReifiedMap.of(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

public fun <Key, Value> KoneMutableReifiedMap.Companion.of(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
    else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality)

public fun <Key, Value> KoneMutableMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableMap<Key, Value>(keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneMutableMap.Companion.contextualOf(
//    vararg entries: KoneMapEntry<Key, Value>,
//): KoneMutableMap<Key, Value> =
//    KoneMutableMap.of(
//        entries = entries,
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//    )

public inline fun <reified Key, Value> KoneMutableReifiedMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    KoneMutableReifiedMap.of(
        entries = entries,
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
    )

public fun <Key, Value> KoneMutableReifiedMap.Companion.of(
    vararg entries: KoneMapEntry<Key, Value>,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
): KoneMutableReifiedMap<Key, Value> =
    if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing).apply { setAllFrom(KoneArray(entries)) }
    else KoneListBackedMutableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality).apply { setAllFrom(KoneArray(entries)) }

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public fun <@Supply Key, Value> KoneMutableReifiedMap.Companion.contextualOf(
//    vararg entries: KoneMapEntry<Key, Value>,
//): KoneMutableReifiedMap<Key, Value> =
//    KoneMutableReifiedMap.of(
//        entries = entries,
//        keyReification = Reification.getFor(),
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//    )

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneMapBuilder<Key, Value> @PublishedApi internal constructor(result: KoneMutableMap<Key, Value>) : KoneMutableMap<Key, Value> {
    private var result: KoneMutableMap<Key, Value>? = result
    
    override val size: UInt get() {
        val result = result ?: error("This KoneMap builder is already used")
        return result.size
    }
    
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result ?: error("This KoneMap builder is already used")
        return result.nodesView
    }
    
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result ?: error("This KoneMap builder is already used")
        return result.nodes
    }
    
    override val keysView: KoneSet<Key> get() {
        val result = result ?: error("This KoneMap builder is already used")
        return result.keysView
    }
    
    override val keys: KoneSet<Key> get() {
        val result = result ?: error("This KoneMap builder is already used")
        return result.keys
    }
    
    override val valuesView: KoneIterable<Value> get() {
        val result = result ?: error("This KoneMap builder is already used")
        return result.valuesView
    }
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? {
        val result = result ?: error("This KoneMap builder is already used")
        return result.getNodeOrNull(key)
    }
    
    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val result = result ?: error("This KoneMap builder is already used")
        return result.set(key, value)
    }
    
    override fun remove(key: Key) {
        val result = result ?: error("This KoneMap builder is already used")
        result.remove(key)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneMap builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Key, Value) -> Boolean) {
        val result = result ?: error("This KoneMap builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllNodesThat(predicate: (KoneMutableMapNode<Key, Value>) -> Boolean) {
        val result = result ?: error("This KoneMap builder is already used")
        result.removeAllNodesThat(predicate)
    }
    
    public operator fun KoneMapEntry<Key, Value>.unaryPlus() {
        val result = result ?: error("This KoneMap builder is already used")
        result.set(this)
    }
    
    public operator fun KoneIterable<KoneMapEntry<Key, Value>>.unaryPlus() {
        val result = result ?: error("This KoneMap builder is already used")
        result.setAllFrom(this)
    }
    
    public operator fun KoneMap<out Key, Value>.unaryPlus() {
        val result = result ?: error("This KoneMap builder is already used")
        result.setAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneMap<Key, Value> {
        val result = result ?: error("This KoneMap builder is already used")
        return result.also { this.result = null }
    }
}

@OptIn(DelicateCollectionsInheritanceAPI::class)
public class KoneReifiedMapBuilder<Key, Value> @PublishedApi internal constructor(result: KoneMutableReifiedMap<Key, Value>) : KoneMutableReifiedMap<Key, Value> {
    private var result: KoneMutableReifiedMap<Key, Value>? = result
    
    override val size: UInt get() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.size
    }
    
    override val nodesView: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.nodesView
    }
    
    override val nodes: KoneReifiedSet<KoneMutableMapNode<Key, Value>> get() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.nodes
    }
    
    override val keysView: KoneReifiedSet<Key> get() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.keysView
    }
    
    override val keys: KoneReifiedSet<Key> get() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.keys
    }
    
    override val valuesView: KoneIterable<Value> get() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.valuesView
    }
    
    override fun getNodeOrNull(key: Key): KoneMutableMapNode<Key, Value>? {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.getNodeOrNull(key)
    }
    
    @IgnorableReturnValue
    override fun set(key: Key, value: Value): KoneMutableMapNode<Key, Value> {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.set(key, value)
    }
    
    override fun remove(key: Key) {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.remove(key)
    }
    
    override fun removeAll() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.removeAll()
    }
    
    override fun removeAllThat(predicate: (Key, Value) -> Boolean) {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.removeAllThat(predicate)
    }
    
    override fun removeAllNodesThat(predicate: (KoneMutableMapNode<Key, Value>) -> Boolean) {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.removeAllNodesThat(predicate)
    }
    
    public operator fun KoneMapEntry<Key, Value>.unaryPlus() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.set(this)
    }
    
    public operator fun KoneIterable<KoneMapEntry<Key, Value>>.unaryPlus() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.setAllFrom(this)
    }
    
    public operator fun KoneMap<out Key, Value>.unaryPlus() {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        result.setAllFrom(this)
    }
    
    @PublishedApi
    internal fun build(): KoneReifiedMap<Key, Value> {
        val result = result ?: error("This KoneReifiedMap builder is already used")
        return result.also { this.result = null }
    }
}

public inline fun <Key, Value> KoneMap.Companion.build(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    builderAction: KoneMapBuilder<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing)
        else KoneListBackedMutableMap(keyEquality = keyEquality, KoneArrayResizableLinkedNoddedListProducer)
    return KoneMapBuilder(mapBuilder).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Key, Value> KoneMap.Companion.buildContextual(
//    builderAction: KoneMapBuilder<Key, Value>.() -> Unit
//): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
//    KoneMap.build(
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//        builderAction = builderAction
//    )

public inline fun <reified Key, Value> KoneReifiedMap.Companion.build(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneReifiedMap.build<Key, Value>(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        builderAction
    )

public inline fun <Key, Value> KoneReifiedMap.Companion.build(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing)
        else KoneListBackedMutableReifiedMap(keyReification = keyReification, keyEquality = keyEquality, KoneArrayResizableLinkedNoddedListProducer)
    return KoneReifiedMapBuilder(mapBuilder).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Key, Value> KoneReifiedMap.Companion.buildContextual(
//    builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
//): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
//    KoneReifiedMap.build(
//        keyReification = Reification.getFor(),
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//        builderAction = builderAction
//    )

public inline fun <Key, Value> KoneMap.Companion.build(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    builderAction: KoneMapBuilder<Key, Value>.() -> Unit
): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableMap<Key, Value>(keyEquality = keyEquality, keyHashing = keyHashing) // TODO: Replace with growable hash map
        else KoneListBackedMutableMap(initialCapacity = initialCapacity, keyEquality = keyEquality, KoneArrayGrowableLinkedNoddedListProducer)
    return KoneMapBuilder(mapBuilder).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Key, Value> KoneMap.Companion.buildContextual(
//    initialCapacity: UInt,
//    builderAction: KoneMapBuilder<Key, Value>.() -> Unit
//): KoneMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
//    KoneMap.build(
//        initialCapacity = initialCapacity,
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//        builderAction = builderAction
//    )

public inline fun <reified Key, Value> KoneReifiedMap.Companion.build(
    initialCapacity: UInt,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
    KoneReifiedMap.build<Key, Value>(
        initialCapacity = initialCapacity,
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        builderAction
    )

public inline fun <Key, Value> KoneReifiedMap.Companion.build(
    initialCapacity: UInt,
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] {
    val mapBuilder =
        if (keyHashing != null) KoneHashResizableReifiedMap<Key, Value>(keyReification = keyReification, keyEquality = keyEquality, keyHashing = keyHashing) // TODO: Replace with growable hash map
        else KoneListBackedMutableReifiedMap(initialCapacity = initialCapacity, keyReification = keyReification, keyEquality = keyEquality, KoneArrayGrowableLinkedNoddedListProducer)
    return KoneReifiedMapBuilder(mapBuilder).apply(builderAction).build()
}

// FIXME: Wait for KT-73135
//@Suppliable
//context(koneContextRegistry: KoneContextRegistry)
//public inline fun <@Supply Key, Value> KoneReifiedMap.Companion.buildContextual(
//    initialCapacity: UInt,
//    builderAction: KoneReifiedMapBuilder<Key, Value>.() -> Unit
//): KoneReifiedMap<Key, Value> contract [ callsInPlace(builderAction, InvocationKind.EXACTLY_ONCE) ] =
//    KoneReifiedMap.build(
//        initialCapacity = initialCapacity,
//        keyReification = Reification.getFor(),
//        keyEquality = Equality.getFor(),
//        keyHashing = Hashing.getForOrNull(),
//        keyOrder = Order.getForOrNull(),
//        builderAction = builderAction
//    )

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
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (Element) -> KoneMapEntry<Key, Value>
): KoneMap<Key, Value> =
    associateTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder
        ),
        transform = transform,
    )

public inline fun <Element, reified Key, Value> KoneIterable<Element>.associateReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (Element) -> KoneMapEntry<Key, Value>,
): KoneReifiedMap<Key, Value> =
    associateReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        transform = transform
    )

public inline fun <Element, Key, Value> KoneIterable<Element>.associateReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (Element) -> KoneMapEntry<Key, Value>
): KoneReifiedMap<Key, Value> =
    associateTo(
        KoneMutableReifiedMap.of(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder
        ),
        transform = transform,
    )

public inline fun <Element, Key> KoneIterable<Element>.associateBy(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key
): KoneMap<Key, Element> =
    associateByTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
    )

public inline fun <Element, reified Key> KoneIterable<Element>.associateByReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key
): KoneReifiedMap<Key, Element> =
    associateByReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        keySelector = keySelector
    )

public inline fun <Element, Key> KoneIterable<Element>.associateByReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
): KoneReifiedMap<Key, Element> =
    associateByTo(
        KoneMutableReifiedMap.of(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
    )

public inline fun <Element, Key, Value> KoneIterable<Element>.associateBy(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
    valueTransform: (Element) -> Value
): KoneMap<Key, Value> =
    associateByTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

public inline fun <Element, reified Key, Value> KoneIterable<Element>.associateByReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
    valueTransform: (Element) -> Value
): KoneReifiedMap<Key, Value> =
    associateByReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        keySelector = keySelector,
        valueTransform = valueTransform
    )

public inline fun <Element, Key, Value> KoneIterable<Element>.associateByReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    keySelector: (Element) -> Key,
    valueTransform: (Element) -> Value
): KoneReifiedMap<Key, Value> =
    associateByTo(
        KoneMutableReifiedMap.of(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        keySelector = keySelector,
        valueTransform = valueTransform,
    )

public inline fun <Key, Value> KoneIterable<Key>.associateWith(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    valueSelector: (Key) -> Value
): KoneMap<Key, Value> =
    associateWithTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        valueSelector = valueSelector,
    )

public inline fun <reified Key, Value> KoneIterable<Key>.associateWithReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    valueSelector: (Key) -> Value,
): KoneReifiedMap<Key, Value> =
    associateWithReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        valueSelector = valueSelector,
    )

public inline fun <Key, Value> KoneIterable<Key>.associateWithReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    valueSelector: (Key) -> Value
): KoneReifiedMap<Key, Value> =
    associateWithTo(
        KoneMutableReifiedMap.of(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        valueSelector = valueSelector,
    )

@IgnorableReturnValue
public inline fun <K, V, W, D : KoneMutableMap<in K, in W>> KoneMap<out K, V>.mapValuesTo(destination: D, transform: (KoneMapEntry<K, V>) -> W): D =
    nodesView.associateByTo(destination, { it.key }, transform)

public inline fun <Key, V, W> KoneMap<out Key, V>.mapValues(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (KoneMapEntry<Key, V>) -> W
): KoneMap<Key, W> =
    mapValuesTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        transform = transform,
    )

public inline fun <reified Key, V, W> KoneMap<out Key, V>.mapValuesReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (KoneMapEntry<Key, V>) -> W
): KoneReifiedMap<Key, W> =
    mapValuesReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        transform = transform,
    )

public inline fun <Key, V, W> KoneMap<out Key, V>.mapValuesReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    transform: (KoneMapEntry<Key, V>) -> W
): KoneReifiedMap<Key, W> =
    mapValuesTo(
        KoneMutableReifiedMap.of(
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
    keyEquality: Equality<L> = Equality.defaultFor(),
    keyHashing: Hashing<L>? = null,
    keyOrder: Order<L>? = null,
    transform: (KoneMapEntry<K, V>) -> L
): KoneMap<L, V> =
    mapKeysTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        transform = transform,
    )

public inline fun <K, V, reified L> KoneMap<out K, V>.mapKeysReified(
    keyEquality: Equality<L> = Equality.defaultFor(),
    keyHashing: Hashing<L>? = null,
    keyOrder: Order<L>? = null,
    transform: (KoneMapEntry<K, V>) -> L
): KoneReifiedMap<L, V> =
    mapKeysReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        transform = transform,
    )


public inline fun <K, V, L> KoneMap<out K, V>.mapKeysReified(
    keyReification: Reification<L>,
    keyEquality: Equality<L> = Equality.defaultFor(),
    keyHashing: Hashing<L>? = null,
    keyOrder: Order<L>? = null,
    transform: (KoneMapEntry<K, V>) -> L
): KoneReifiedMap<L, V> =
    mapKeysTo(
        KoneMutableReifiedMap.of(
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
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Key) -> Boolean
): KoneMap<Key, V> =
    filterKeysTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate,
    )

public inline fun <reified Key, V> KoneMap<out Key, V>.filterKeysReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Key) -> Boolean
): KoneReifiedMap<Key, V> =
    filterKeysReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        predicate = predicate,
    )

public inline fun <Key, V> KoneMap<out Key, V>.filterKeysReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Key) -> Boolean
): KoneReifiedMap<Key, V> =
    filterKeysTo(
        KoneMutableReifiedMap.of(
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
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (V) -> Boolean
): KoneMap<Key, V> =
    filterValuesTo(
        KoneMutableMap.of(
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate,
    )

public inline fun <reified Key, V> KoneMap<out Key, V>.filterValuesReified(
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (V) -> Boolean
): KoneReifiedMap<Key, V> =
    filterValuesReified(
        keyReification = Reification.defaultFor(),
        keyEquality = keyEquality,
        keyHashing = keyHashing,
        keyOrder = keyOrder,
        predicate = predicate,
    )

public inline fun <Key, Value> KoneMap<out Key, Value>.filterValuesReified(
    keyReification: Reification<Key>,
    keyEquality: Equality<Key> = Equality.defaultFor(),
    keyHashing: Hashing<Key>? = null,
    keyOrder: Order<Key>? = null,
    predicate: (Value) -> Boolean)
: KoneReifiedMap<Key, Value> =
    filterValuesTo(
        KoneMutableReifiedMap.of(
            keyReification = keyReification,
            keyEquality = keyEquality,
            keyHashing = keyHashing,
            keyOrder = keyOrder,
        ),
        predicate = predicate
    )