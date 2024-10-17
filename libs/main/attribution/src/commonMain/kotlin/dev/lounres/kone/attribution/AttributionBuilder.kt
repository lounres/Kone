/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.attribution

import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.KoneMutableMap
import dev.lounres.kone.collections.getOrNull
import dev.lounres.kone.collections.koneMutableMapOf
import dev.lounres.kone.comparison.absoluteEquality
import dev.lounres.kone.option.Option
import kotlin.jvm.JvmInline


@Suppress("UNCHECKED_CAST")
@JvmInline
public value class AttributionBuilder<@Suppress("unused") out AO> @PublishedApi internal constructor(
    @PublishedApi
    internal val attributeStorage: KoneMutableMap<Attribute<*>, Any?> = koneMutableMapOf(keyContext = absoluteEquality())
)/*: Attribution*/ {
    public operator fun contains(attribute: Attribute<*>): Boolean = attributeStorage.containsKey(attribute)
    public operator fun <T> get(attribute: Attribute<out T>): T = attributeStorage[attribute] as T
    public fun <T> getOrNull(attribute: Attribute<T>): T? = attributeStorage.getOrNull(attribute) as T?
    public fun <T> getMaybe(attribute: Attribute<T>): Option<T> = attributeStorage.getMaybe(attribute) as Option<T>
    
    public fun asKoneMap(): KoneMap<Attribute<*>, Any?> = attributeStorage
    
    public infix fun <T> Attribute<T>.provides(value: T) {
        attributeStorage[this] = value
    }
    
    public fun addAllFrom(otherAttribution: Attribution) {
        attributeStorage.setAllFrom(otherAttribution.asKoneMap())
    }
}

public inline fun <AS> Attribution(builder: AttributionBuilder<AS>.() -> Unit): Attribution =
    Attribution(AttributionBuilder<AS>().apply(builder).attributeStorage)