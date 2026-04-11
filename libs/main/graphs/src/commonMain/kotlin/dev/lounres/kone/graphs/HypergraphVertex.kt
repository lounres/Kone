/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.registry.MutableOwnedProviderRegistry
import dev.lounres.kone.registry.OwnedProviderRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.getOrElse
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


@Suppress("EqualsOrHashCode")
public class HypergraphVertex(
    public val properties: OwnedProviderRegistry<HypergraphVertex> = OwnedProviderRegistry.empty(),
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
    
    public data object NameKey : RegistryKey<String> {
        override fun toString(): String = "dev.lounres.kone.graphs.HypergraphVertex.NameKey"
    }
    
    private class Delegate(val vertex: HypergraphVertex) : ReadOnlyProperty<Any?, HypergraphVertex> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): HypergraphVertex = vertex
    }
}

public fun HypergraphVertex(propertiesBuilder: MutableOwnedProviderRegistry<HypergraphVertex>.() -> Unit): HypergraphVertex =
    HypergraphVertex(
        properties = OwnedProviderRegistry.build(propertiesBuilder),
    )

public val OwnedProviderRegistry<HypergraphVertex>.name: String
    @JvmName("getHypergraphVertexOwnedRegistryName") get() = get(HypergraphVertex.NameKey)

public var MutableOwnedProviderRegistry<HypergraphVertex>.name: String
    @JvmName("getHypergraphVertexMutableOwnedRegistryName") get() = get(HypergraphVertex.NameKey)
    @JvmName("setHypergraphVertexMutableOwnedRegistryName") set(value) { set(HypergraphVertex.NameKey, value) }

public val HypergraphVertex.name: String
    @JvmName("getHypergraphVertexName") get() = properties.name