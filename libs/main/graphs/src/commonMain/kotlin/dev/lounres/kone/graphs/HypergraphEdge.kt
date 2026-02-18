/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.registry.MutableOwnedRegistry
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.get
import dev.lounres.kone.registry.getOrElse
import dev.lounres.kone.registry.set
import kotlin.jvm.JvmName
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


@Suppress("EqualsOrHashCode")
public class HypergraphEdge(
    public val vertices: KoneList<HypergraphVertex>,
    public val properties: OwnedRegistry<HypergraphEdge> = OwnedRegistry.empty(),
) {
    override fun equals(other: Any?): Boolean = this === other
    
    override fun toString(): String = properties.getOrElse(NameKey) { super.toString() }
    
    public operator fun provideDelegate(thisRef: Any?, property: KProperty<*>): ReadOnlyProperty<Any?, HypergraphEdge> =
        Delegate(
            HypergraphEdge(
                vertices = vertices,
            ) {
                setFrom(properties)
                name = property.name
            }
        )
    
    public companion object;
    
    public data object NameKey : RegistryKey<String> {
        override fun toString(): String = "dev.lounres.kone.graphs.HypergraphEdge.NameKey"
    }
    
    private class Delegate(val edge: HypergraphEdge) : ReadOnlyProperty<Any?, HypergraphEdge> {
        override fun getValue(thisRef: Any?, property: KProperty<*>): HypergraphEdge = edge
    }
}

public inline fun HypergraphEdge(
    vertices: KoneList<HypergraphVertex>,
    propertiesBuilder: MutableOwnedRegistry<HypergraphEdge>.() -> Unit
): HypergraphEdge =
    HypergraphEdge(
        vertices = vertices,
        properties = OwnedRegistry.build(propertiesBuilder),
    )

public fun HypergraphEdge(
    vararg vertices: HypergraphVertex,
    properties: OwnedRegistry<HypergraphEdge> = OwnedRegistry.empty(),
): HypergraphEdge =
    HypergraphEdge(
        vertices = KoneArray(vertices),
        properties = properties,
    )

public inline fun HypergraphEdge(
    vararg vertices: HypergraphVertex,
    propertiesBuilder: MutableOwnedRegistry<HypergraphEdge>.() -> Unit
): HypergraphEdge =
    HypergraphEdge(
        vertices = KoneArray(vertices),
        properties = OwnedRegistry.build(propertiesBuilder),
    )

public fun HypergraphEdge.Companion.directed(
    start: HypergraphVertex,
    end: HypergraphVertex,
    properties: OwnedRegistry<HypergraphEdge> = OwnedRegistry.empty(),
): HypergraphEdge =
    HypergraphEdge(start, end) {
        setFrom(properties)
        GraphEdgeDirection.Key correspondsTo GraphEdgeDirection.FromFirstToSecond
    }

public inline fun HypergraphEdge.Companion.directed(
    start: HypergraphVertex,
    end: HypergraphVertex,
    propertiesBuilder: MutableOwnedRegistry<HypergraphEdge>.() -> Unit,
): HypergraphEdge =
    HypergraphEdge(start, end) {
        propertiesBuilder()
        GraphEdgeDirection.Key correspondsTo GraphEdgeDirection.FromFirstToSecond
    }

public val OwnedRegistry<HypergraphEdge>.name: String
    @JvmName("getHypergraphEdgeOwnedRegistryName") get() = get(HypergraphEdge.NameKey)

public var MutableOwnedRegistry<HypergraphEdge>.name: String
    @JvmName("getHypergraphEdgeMutableOwnedRegistryName") get() = get(HypergraphEdge.NameKey)
    @JvmName("setHypergraphEdgeMutableOwnedRegistryName") set(value) { set(HypergraphEdge.NameKey, value) }

public val HypergraphEdge.name: String
    @JvmName("getHypergraphEdgeName") get() = properties.name