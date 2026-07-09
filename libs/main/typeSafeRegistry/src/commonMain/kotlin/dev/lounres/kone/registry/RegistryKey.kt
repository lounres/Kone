/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry


/**
 * A key that is used to retrieve a value of type [T] from [ProviderRegistry].
 */
public interface RegistryKey<T> {
    /**
     * Key context that describes equality between this key and the others.
     */
    public val context: RegistryKeyContext get() = NaiveRegistryKeyContext
    public val impliedKeys: ImpliedKeysRegistry<T> get() = Empty
    
    public companion object;
}