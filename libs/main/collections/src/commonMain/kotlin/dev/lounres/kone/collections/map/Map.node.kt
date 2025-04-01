/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map


// TODO: Maybe make `KoneMapEntry` an interface that `KoneMapNode` can inherit?
// TODO: Describe contracts on equals and hashCode.

public interface KoneMapEntry<out Key, out Value> {
    public val key: Key
    public val value: Value
}

internal data class KoneMapEntryImpl<out Key, out Value>(
    override val key: Key,
    override val value: Value
): KoneMapEntry<Key, Value> {
    override fun toString(): String = "$key=$value"
}

public fun <Key, Value> KoneMapEntry(key: Key, value: Value): KoneMapEntry<Key, Value> = KoneMapEntryImpl(key, value)
public infix fun <Key, Value> Key.mapsTo(value: Value): KoneMapEntry<Key, Value> = KoneMapEntryImpl(this, value)

// TODO: Describe contracts on equals and hashCode.

public interface KoneMapNode<out Key, out Value> : KoneMapEntry<Key, Value> {
    public val isDetached: Boolean
    
    override val key: Key
    override val value: Value
}

public interface KoneMutableMapNode<out Key, Value> : KoneMapNode<Key, Value> {
    override var value: Value
    public fun remove()
}