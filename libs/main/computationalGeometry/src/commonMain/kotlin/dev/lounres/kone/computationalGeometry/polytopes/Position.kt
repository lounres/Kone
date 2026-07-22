/*
 * Copyright © 2026 Gleb Minaev
 * All rights reserved. Licensed under the Apache License, Version 2.0. See the license in file LICENSE
 */

package dev.lounres.kone.computationalGeometry.polytopes

import dev.lounres.kone.registry.SuppliedTypeRegistryKey
import dev.lounres.kone.suppliedTypes.Suppliable
import dev.lounres.kone.suppliedTypes.Supply
import dev.lounres.kone.suppliedTypes.suppliedTypeOf


@Suppliable
public class Position<@Supply Point> : SuppliedTypeRegistryKey<Point>() {
    override fun toString(): String = "dev.lounres.kone.computationalGeometry.polytopes.Position<${suppliedTypeOf<Point>()}>"
}

// TODO: Remove the checker when KT-73135 will be fixed
public object PolytopePositionOfTypeSuppliableTopLevelFunctions {
    @Suppliable
    public fun <@Supply Point> Polytope.positionOfType(): Point = properties[Position<Point>()]
}