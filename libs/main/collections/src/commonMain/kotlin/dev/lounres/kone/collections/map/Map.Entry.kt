/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections.map


// TODO: Describe contracts on equals and hashCode.

public data class KoneMapEntry<out Key, out Value>(public val key: Key, public val value: Value) {
    public override fun toString(): String = "$key=$value"
}

public infix fun <Key, Value> Key.mapsTo(value: Value): KoneMapEntry<Key, Value> = KoneMapEntry(this, value)