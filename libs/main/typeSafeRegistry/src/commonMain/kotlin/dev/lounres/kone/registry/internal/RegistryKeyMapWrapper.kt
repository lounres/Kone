/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.registry.internal

import dev.lounres.kone.registry.RegistryKey


/**
 * A wrapper of [RegistryKey] that can be used in Kotlin stdlib's maps.
 */
// TODO: Implement custom map for registry keys and remove `RegistryKeyMapWrapper`.
internal class RegistryKeyMapWrapper<T>(val key: RegistryKey<T>) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RegistryKeyMapWrapper<*>) return false
        val thisEquality = this.key.context
        val otherEquality = other.key.context
        if (thisEquality !== otherEquality) return false
        
        return thisEquality.checkEqualityOf(this.key, other.key)
    }
    override fun hashCode(): Int = key.context.hashCodeOf(key)
    override fun toString(): String = key.toString()
}