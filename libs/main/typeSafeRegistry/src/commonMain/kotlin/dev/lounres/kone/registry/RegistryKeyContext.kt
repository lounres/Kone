/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry


/**
 * Describes how [RegistryKey]s are equated and stored in [ProviderRegistry].
 */
public interface RegistryKeyContext {
    /**
     * Checks equality between the [left] and the [right] keys.
     *
     * This equality is used by [ProviderRegistry] to decide how the keys are stored and what value to retrieve by the key.
     */
    public fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean
    
    /**
     * Defines hash code of the key.
     *
     * This hash code is used by [ProviderRegistry] to decide how the keys are stored and what value to retrieve by the key.
     */
    public fun hashCodeOf(key: RegistryKey<*>): Int
}

/**
 * Naive implementation of the [RegistryKeyContext].
 */
// TODO: Review `NaiveRegistryKeyContext` and all `RegistryKey` implementations
public data object NaiveRegistryKeyContext : RegistryKeyContext {
    override fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean = left::class == right::class && left == right
    override fun hashCodeOf(key: RegistryKey<*>): Int = key.hashCode()
}