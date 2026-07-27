/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.SuppliedType
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


/**
 * Base class for [RegistryKey] implementations whose identity depends on supplied type arguments.
 *
 * Subclasses are typically nested `Key` types inside suppliable declarations and are annotated with
 * [Suppliable]. Two keys are equal when they have the same runtime class and their [Supply]-annotated
 * type parameters are described by equal [SuppliedType] values (obtained via [suppliedTypeOf]).
 * This allows distinct key instances that share the same supplied types to be treated as the same
 * key inside a [ProviderRegistry].
 *
 * @param T The type of the value associated with this key in a registry.
 */
@Suppliable
public open class SuppliedTypeRegistryKey<@Supply T> : RegistryKey<T> {
    private val typeKey: SuppliedType by lazy { suppliedTypeOf<T>() }
    
    /**
     * The [RegistryKeyContext] that defines equality and hashing for this key based on supplied types.
     *
     * Keys are equated by runtime class and by the [SuppliedType] representation of their type
     * parameters, rather than by object identity.
     *
     * @return The [RegistryKeyContext] used when storing and looking up this key in a registry.
     */
    final override val context: RegistryKeyContext get() = Context
    
    /**
     * Checks whether this key is equal to [other].
     *
     * Returns `true` when [other] is the same instance, or when [other] is a [SuppliedTypeRegistryKey]
     * with the same runtime class and equal supplied-type arguments.
     *
     * @param other The object to compare with this key.
     * @return `true` if [other] represents the same supplied-type-aware registry key, `false` otherwise.
     */
    final override fun equals(other: Any?): Boolean = this === other || other is SuppliedTypeRegistryKey<*> && this::class == other::class && typeKey == other.typeKey
    
    /**
     * Returns the hash code of this key.
     *
     * The hash code is derived from the [SuppliedType] representation of this key's type parameters,
     * consistent with [equals].
     *
     * @return The hash code of this key.
     */
    final override fun hashCode(): Int = typeKey.hashCode()
    
    private data object Context : RegistryKeyContext {
        override fun checkEqualityOf(left: RegistryKey<*>, right: RegistryKey<*>): Boolean {
            if (left === right) return true
            if (left::class != right::class) return false
            check(left is SuppliedTypeRegistryKey<*> && right is SuppliedTypeRegistryKey<*>) { TODO() }
            return left.typeKey == right.typeKey
        }
        override fun hashCodeOf(key: RegistryKey<*>): Int {
            check(key is SuppliedTypeRegistryKey<*>) { TODO() }
            return key.typeKey.hashCode()
        }
    }
}