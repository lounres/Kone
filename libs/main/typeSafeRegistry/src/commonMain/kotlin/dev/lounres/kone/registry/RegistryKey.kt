/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry


/**
 * A key that is used to retrieve a value of type [T] from a [ProviderRegistry].
 * 
 * Registry keys serve as type-safe identifiers for values stored in registries.
 * Each key has an associated context that determines how equality and hashing are computed.
 * 
 * @param T The type of the value associated with this key.
 */
public interface RegistryKey<T> {
    /**
     * Key context that describes equality between this key and other keys.
     * 
     * The context determines how this key is compared with other keys for equality
     * and how its hash code is computed. This affects how keys are stored and retrieved in registries.
     * 
     * @return The [RegistryKeyContext] associated with this key. Defaults to [NaiveRegistryKeyContext].
     */
    public val context: RegistryKeyContext get() = NaiveRegistryKeyContext
    
    /**
     * Registry of keys that can be implied from this key.
     * 
     * When this key is used in certain contexts, the implied keys can be automatically
     * derived or computed based on the value associated with this key.
     * 
     * @return An [ImpliedKeysRegistry] containing keys that can be implied from this key.
     * By default, returns [ImpliedKeysRegistry.Empty].
     */
    public val impliedKeys: ImpliedKeysRegistry<T> get() = Empty
    
    public companion object;
}