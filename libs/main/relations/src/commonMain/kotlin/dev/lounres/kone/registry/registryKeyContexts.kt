/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry

import dev.lounres.kone.relations.Equality
import dev.lounres.kone.relations.Hashing


private object RegistryKeyEquality : Equality<RegistryKey<*>> {
    override fun RegistryKey<*>.equalsTo(other: RegistryKey<*>): Boolean {
        if (this === other) return true
        val thisContext = this.context
        val otherContext = other.context
        if (thisContext !== otherContext) return false
        return thisContext.checkEqualityOf(this, other)
    }
}

public fun RegistryKey.Companion.equality(): Equality<RegistryKey<*>> = RegistryKeyEquality

private object RegistryKeyHashing : Hashing<RegistryKey<*>> {
    override fun RegistryKey<*>.hash(): Int = this.context.hashCodeOf(this)
}

public fun RegistryKey.Companion.hashing(): Hashing<RegistryKey<*>> = RegistryKeyHashing