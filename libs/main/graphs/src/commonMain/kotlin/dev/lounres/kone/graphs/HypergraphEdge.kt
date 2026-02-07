/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.array.KoneArray
import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.registry.OwnedRegistry
import dev.lounres.kone.registry.OwnedRegistryBuilder
import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.build
import dev.lounres.kone.registry.correspondsTo
import dev.lounres.kone.registry.empty
import dev.lounres.kone.registry.getOrElse


@Suppress("EqualsOrHashCode")
public class HypergraphEdge(
    public val vertices: KoneList<HypergraphVertex>,
    public val properties: OwnedRegistry<HypergraphEdge> = OwnedRegistry.empty(),
) {
    override fun equals(other: Any?): Boolean = this === other
    
    override fun toString(): String = properties.getOrElse(NameKey) { super.toString() }
    
    public companion object;
    
    public data object NameKey : RegistryKey<String>
}

public inline fun HypergraphEdge(
    vertices: KoneList<HypergraphVertex>,
    propertiesBuilder: OwnedRegistryBuilder<HypergraphEdge>.() -> Unit
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
    propertiesBuilder: OwnedRegistryBuilder<HypergraphEdge>.() -> Unit
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
    propertiesBuilder: OwnedRegistryBuilder<HypergraphEdge>.() -> Unit,
): HypergraphEdge =
    HypergraphEdge(start, end) {
        propertiesBuilder()
        GraphEdgeDirection.Key correspondsTo GraphEdgeDirection.FromFirstToSecond
    }