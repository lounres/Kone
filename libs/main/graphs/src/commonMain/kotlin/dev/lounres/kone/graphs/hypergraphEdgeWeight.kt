package dev.lounres.kone.graphs

import dev.lounres.kone.registry.RegistryKey
import dev.lounres.kone.registry.get
import dev.lounres.kone.suppliedTypes.SuppliedType


public class HypergraphEdgeWeightKey<Weight>(public val weightType: SuppliedType) : RegistryKey<Weight> {
    override fun equals(other: Any?): Boolean = other is HypergraphEdgeWeightKey<*> && weightType == other.weightType
    override fun hashCode(): Int = weightType.hashCode()
    override fun toString(): String = "dev.lounres.kone.relations.Equality.Key<$weightType>"
}

public fun <Weight> HypergraphEdge.weightOfType(weightType: SuppliedType): Weight = properties[HypergraphEdgeWeightKey<Weight>(weightType)]