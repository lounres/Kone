/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.suppliedTypes.SuppliedType


public class HypergraphEdgeWeightKey<Weight>(public val weightType: SuppliedType) : RegistryKey<Weight> {
    override fun equals(other: Any?): Boolean = other is HypergraphEdgeWeightKey<*> && weightType == other.weightType
    override fun hashCode(): Int = weightType.hashCode()
    override fun toString(): String = "dev.lounres.kone.relations.Equality.Key<$weightType>"
}

public fun <Weight> HypergraphEdge.weightOfType(weightType: SuppliedType): Weight = properties[HypergraphEdgeWeightKey<Weight>(weightType)]