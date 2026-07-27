/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry


/**
 * Describes how [RegistryKey] instances are equated and stored in a [ProviderRegistry].
 * 
 * Implementations of this interface define the equality and hashing semantics for registry keys,
 * which affects how keys are stored, indexed, and retrieved in registries.
 */
public interface RegistryKeyContext {
    /**
     * Checks equality between the [left] and the [right] keys.
     * 
     * This equality is used by [ProviderRegistry] to decide how the keys are stored and what value to retrieve by the key.
     * 
     * @param left The left [RegistryKey] to compare.
     * @param right The right [RegistryKey] to compare.
     * @return `true` if the keys are considered equal, `false` otherwise.
     */
    public fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean
    
    /**
     * Defines hash code of the specified key.
     * 
     * This hash code is used by [ProviderRegistry] to decide how the keys are stored and what value to retrieve by the key.
     * 
     * @param key The [RegistryKey] for which to compute the hash code.
     * @return The hash code for the specified [key].
     */
    public fun hashCodeOf(key: RegistryKey<*>): Int
}

/**
 * Naive implementation of the [RegistryKeyContext].
 * 
 * This context uses both class equality and object equality to determine if two keys are equal.
 * It uses the standard [Any.hashCode] of the key object for hashing.
 */
// TODO: Review `NaiveRegistryKeyContext` and all `RegistryKey` implementations
public data object NaiveRegistryKeyContext : RegistryKeyContext {
    /**
     * Checks if two keys are equal by comparing both their classes and their object equality.
     * 
     * @param left The left [RegistryKey] to compare.
     * @param right The right [RegistryKey] to compare.
     * @return `true` if both keys are of the same class and are equal according to their [equals][Any.equals] method.
     */
    override fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean = left::class == right::class && left == right
    
    /**
     * Returns the hash code of the key using its standard [hashCode][Any.hashCode] method.
     * 
     * @param key The [RegistryKey] to compute the hash code for.
     * @return The hash code of the [key].
     */
    override fun hashCodeOf(key: RegistryKey<*>): Int = key.hashCode()
}