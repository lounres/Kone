/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.SuppliedType
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public open class SuppliedTypeRegistryKey<@Supply T> : RegistryKey<T> {
    private val typeKey: SuppliedType by lazy { suppliedTypeOf<T>() }
    final override val context: RegistryKeyContext get() = NaiveRegistryKeyContext
    final override fun equals(other: Any?): Boolean = other is SuppliedTypeRegistryKey<*> && this::class == other::class && typeKey == other.typeKey
    final override fun hashCode(): Int = typeKey.hashCode()
}