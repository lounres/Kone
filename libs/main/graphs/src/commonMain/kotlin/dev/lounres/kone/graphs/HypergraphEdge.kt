/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.collections.list.KoneList
import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.build


@Suppress("EqualsOrHashCode")
public class HypergraphEdge(
    public val vertices: KoneList<HypergraphVertex>,
    public val properties: Registry = Registry.Empty,
) {
    override fun equals(other: Any?): Boolean = this === other
}

public fun HypergraphEdge(
    vertices: KoneList<HypergraphVertex>,
    propertiesBuilder: RegistryBuilder<HypergraphEdge>.() -> Unit
): HypergraphEdge =
    HypergraphEdge(
        vertices = vertices,
        properties = Registry.build(propertiesBuilder),
    )