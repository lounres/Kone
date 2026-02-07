/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.getOrElse
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


@Suppress("EqualsOrHashCode")
public class HypergraphVertex(
    public val properties: OwnedRegistry<HypergraphVertex> = OwnedRegistry.empty(),
) {
    override fun equals(other: Any?): Boolean = this === other
    
    override fun toString(): String = properties.getOrElse(NameKey) { super.toString() }
    
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, HypergraphVertex> =
        Delegate(
            HypergraphVertex {
                setFrom(properties)
                name = property.name
            }
        )
    
    public companion object {
        public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, HypergraphVertex> =
            Delegate(
                HypergraphVertex {
                    name = property.name
                }
            )
    }
    
    public data object NameKey : RegistryKey<String>
    
    private class Delegate(val vertex: HypergraphVertex) : ReadOnlyProperty<Any?, HypergraphVertex> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): HypergraphVertex = vertex
    }
}

public fun HypergraphVertex(propertiesBuilder: OwnedRegistryBuilder<HypergraphVertex>.() -> Unit): HypergraphVertex =
    HypergraphVertex(
        properties = OwnedRegistry.build(propertiesBuilder),
    )