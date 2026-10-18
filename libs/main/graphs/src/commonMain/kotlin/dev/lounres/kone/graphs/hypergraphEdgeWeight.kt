/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.graphs

import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.SuppliedType
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.of


@Suppliable
public class HypergraphEdgeWeightKey<@Supply Weight> : SuppliedTypeRegistryKey<Weight>() {
    override fun toString(): String = "dev.lounres.kone.graphs.HypergraphEdgeWeightKey.Key<${SuppliedType.of<Weight>()}>"
}

// TODO: Remove the checker when KT-73135 will be fixed
public object HypergraphEdgeWeightOfTypeSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Weight> HypergraphEdge.weightOfType(): Weight = properties[HypergraphEdgeWeightKey<Weight>()]
}