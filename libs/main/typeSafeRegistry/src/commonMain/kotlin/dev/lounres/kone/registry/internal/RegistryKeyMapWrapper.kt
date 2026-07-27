/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry.internal

import dev.lounres.kone.registry.RegistryKey


/**
 * A wrapper of [RegistryKey] that can be used in Kotlin stdlib's maps.
 * 
 * This wrapper uses the [RegistryKeyContext] associated with the key to implement custom
 * equality and hashing, allowing registry keys to be used as map keys while respecting
 * their custom semantics.
 * 
 * @param T The type of the value associated with the wrapped [RegistryKey].
 * @param key The [RegistryKey] to wrap.
 */
// TODO: Implement custom map for registry keys and remove `RegistryKeyMapWrapper`.
internal class RegistryKeyMapWrapper<T>(val key: RegistryKey<T>) {
    /**
     * Checks equality with the specified [other] object using the [RegistryKeyContext] semantics.
     * 
     * Two wrappers are considered equal if they wrap keys with the same context and
     * the context considers the keys equal.
     * 
     * @param other The object to compare with this wrapper.
     * @return `true` if the [other] is a [RegistryKeyMapWrapper] with an equal key, `false` otherwise.
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegistryKeyMapWrapper<*>) return false
        val thisEquality = this.key.context
        val otherEquality = other.key.context
        if (thisEquality !== otherEquality) return false
        
        return thisEquality.checkEqualityOf(this.key, other.key)
    }
    
    /**
     * Computes the hash code for this wrapper using the [RegistryKeyContext] semantics.
     * 
     * @return The hash code computed by the key's context.
     */
    override fun hashCode(): Int = key.context.hashCodeOf(key)
    
    /**
     * Returns the string representation of this wrapper.
     * 
     * @return The string representation of the wrapped key.
     */
    override fun toString(): String = key.toString()
}