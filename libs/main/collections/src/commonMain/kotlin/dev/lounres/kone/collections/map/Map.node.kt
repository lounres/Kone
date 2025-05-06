/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map

import dev.lounres.kone.collections.DetachedNodeException


// TODO: Describe contracts on equals and hashCode.
/**
 * Represents a pair of a key and its (future) value in the map.
 *
 * This interface is used to represent entries for a map when the map is being constructed.
 */
public interface KoneMapEntry<out Key, out Value> {
    /**
     * Returns the corresponding key of the entry.
     */
    public val key: Key
    /**
     * Returns the corresponding value of the entry.
     */
    public val value: Value
    
    public companion object
}

internal data class KoneMapEntryImpl<out Key, out Value>(
    override val key: Key,
    override val value: Value
): KoneMapEntry<Key, Value> {
    override fun toString(): String = "$key=$value"
}

/**
 * Returns simple [KoneMapEntry] instance that has the specified [key] and [value].
 */
public fun <Key, Value> KoneMapEntry(key: Key, value: Value): KoneMapEntry<Key, Value> = KoneMapEntryImpl(key, value)
/**
 * Returns simple [KoneMapEntry] instance that has the specified [key][this] and [value].
 */
public infix fun <Key, Value> Key.mapsTo(value: Value): KoneMapEntry<Key, Value> = KoneMapEntryImpl(this, value)

/**
 * Represents a node in the inner structure of [KoneMap].
 *
 * The node can be detached by removing the corresponding place from the collection it was defined in.
 * In that case only its key and value are preserved
 * and other methods and properties throw [DetachedNodeException].
 *
 * @see KoneMap
 */
public interface KoneMapNode<out Key, out Value> : KoneMapEntry<Key, Value> {
    /**
     * Indicates if the node is detached from the structure it was a part of.
     */
    public val isDetached: Boolean
    
    /**
     * Returns key corresponding to that node.
     *
     * When detached (that happens only when the corresponding place is removed)
     * still holds the key it was holding.
     */
    override val key: Key
    /**
     * Returns value corresponding to that node.
     * Change of the value changes the corresponding value in the structure.
     *
     * When detached (that happens only when the corresponding place is removed)
     * holds the last value it was holding.
     */
    override val value: Value
}

/**
 * Represents a node in the inner structure of [KoneMutableMap].
 * See [KoneMapNode] for general definition.
 *
 * @see KoneMapNode
 * @see KoneMutableMap
 */
public interface KoneMutableMapNode<out Key, Value> : KoneMapNode<Key, Value> {
    /**
     * Returns value corresponding to that node.
     * Change of the value changes the corresponding value in the structure.
     *
     * When detached (that happens only when the corresponding place is removed)
     * holds the last value it was holding.
     * After detaching, the node just stores the element that can be changed.
     * And the changing won't modify the structure the node was detached from.
     */
    override var value: Value
    /**
     * Removes the corresponding place from the heap and detaches the node.
     *
     * The operation must be idempotent.
     * It means that calling this function again must do nothing at all.
     */
    public fun remove()
}