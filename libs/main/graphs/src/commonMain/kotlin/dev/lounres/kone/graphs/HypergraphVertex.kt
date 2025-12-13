/*
 * Copyright © 2025 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.registry.Registry
import dev.lounres.kone.registry.RegistryBuilder
import dev.lounres.kone.registry.build


@Suppress("EqualsOrHashCode")
public class HypergraphVertex(
    public val properties: Registry = Registry.Empty,
) {
    override fun equals(other: Any?): Boolean = this === other
}

public fun HypergraphVertex(propertiesBuilder: RegistryBuilder<HypergraphVertex>.() -> Unit): HypergraphVertex =
    HypergraphVertex(
        properties = Registry.build(propertiesBuilder),
    )