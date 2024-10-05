/*
 * Copyright © 2024 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.attribution

import dev.lounres.kone.collections.KoneMap
import dev.lounres.kone.collections.getOrNull
import dev.lounres.kone.option.Option
import dev.lounres.kone.option.orDefault
import dev.lounres.kone.option.orElse
import kotlin.jvm.JvmInline


@Suppress("UNCHECKED_CAST")
@JvmInline
public value class Attribution @PublishedApi internal constructor(internal val attributeStorage: KoneMap<Attribute<*>, Any?>) {
    public operator fun contains(attribute: Attribute<*>): Boolean = attributeStorage.containsKey(attribute)
    public operator fun <T> get(attribute: Attribute<out T>): T = attributeStorage[attribute] as T
    public fun <T> getOrNull(attribute: Attribute<T>): T? = attributeStorage.getOrNull(attribute) as T?
    public fun <T> getMaybe(attribute: Attribute<T>): Option<T> = attributeStorage.getMaybe(attribute) as Option<T>
    
    public fun asKoneMap(): KoneMap<Attribute<*>, Any?> = attributeStorage
}

public fun <T> Attribution.getOrDefault(attribute: Attribute<T>, default: T): T = getMaybe(attribute).orDefault(default)
public fun <T> Attribution.getOrElse(attribute: Attribute<T>, default: () -> T): T = getMaybe(attribute).orElse(default)