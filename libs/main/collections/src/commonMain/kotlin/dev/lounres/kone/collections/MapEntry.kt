/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.collections


public open class KoneMapEntry<out K, out V>(public val key: K, public open val value: V) {
    public override fun toString(): String = "$key=$value"
    public override fun equals(other: Any?): Boolean = other is KoneMapEntry<*, *> && key == other.key && value == other.value
    public override fun hashCode(): Int = key.hashCode() xor value.hashCode()

    public operator fun component1(): K = key
    public operator fun component2(): V = value
}

public infix fun <K, V> K.mapsTo(value: V): KoneMapEntry<K, V> = KoneMapEntry(this, value)