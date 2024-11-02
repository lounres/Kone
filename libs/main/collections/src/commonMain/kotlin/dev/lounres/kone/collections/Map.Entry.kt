/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


// TODO: Describe contracts on equals and hashCode.

public data class KoneMapEntry<out K, out V>(public val key: K, public val value: V) {
    public override fun toString(): String = "$key=$value"
}

public infix fun <K, V> K.mapsTo(value: V): KoneMapEntry<K, V> = KoneMapEntry(this, value)